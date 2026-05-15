package com.ecommerce.factorIT.Service;

import com.ecommerce.factorIT.Constants;
import com.ecommerce.factorIT.DTO.request.AgregarProductoRequest;
import com.ecommerce.factorIT.DTO.request.CrearCarritoRequest;
import com.ecommerce.factorIT.DTO.response.CarritoResponse;
import com.ecommerce.factorIT.DTO.response.PageResponse;
import com.ecommerce.factorIT.Enum.EstadoCarrito;
import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.Carrito;
import com.ecommerce.factorIT.Model.CarritoDetalle;
import com.ecommerce.factorIT.Model.Cliente;
import com.ecommerce.factorIT.Model.Producto;
import com.ecommerce.factorIT.Repository.CarritoDetalleRepository;
import com.ecommerce.factorIT.Repository.CarritoRepository;
import com.ecommerce.factorIT.Repository.ClienteRepository;
import com.ecommerce.factorIT.Repository.FechaEspecialRepository;
import com.ecommerce.factorIT.Repository.ProductoRepository;
import com.ecommerce.factorIT.Service.strategy.CarritoComunStrategy;
import com.ecommerce.factorIT.Service.strategy.CarritoFechaEspecialStrategy;
import com.ecommerce.factorIT.Service.strategy.CarritoVipStrategy;
import com.ecommerce.factorIT.Service.strategy.DescuentoStrategy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final FechaEspecialRepository fechaEspecialRepository;
    private final CarritoComunStrategy comunStrategy;
    private final CarritoFechaEspecialStrategy fechaEspecialStrategy;
    private final CarritoVipStrategy vipStrategy;

    /**
     * Creates a new cart for the client.
     * The cart type is automatically determined:
     * 1. VIP if the client has VIP status (and didn't skip a month without purchases)
     * 2. FECHA_ESPECIAL if the simulated date is a special promotional date
     * 3. COMUN otherwise
     */
    @Transactional
    public CarritoResponse crearCarrito(CrearCarritoRequest request, LocalDate fechaSimulada) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        LocalDate fecha = fechaSimulada != null ? fechaSimulada : LocalDate.now();

        // Chequeo lazy de pérdida de VIP al crear un carrito: si es VIP pero no compró
        // el mes anterior, le sacamos el flag (para que el nuevo carrito sea COMUN o FECHA_ESPECIAL,
        // no VIP). Los reportes de VIPs por mes NO dependen de este flag: se calculan dinámicamente
        // desde la tabla carritos en ClienteRepository.
        if (cliente.isEsVip()) {
            LocalDate primerDiaMesAnterior = fecha.minusMonths(1).withDayOfMonth(1);
            long comprasMesAnterior = carritoRepository.countCompletadoByClienteAndMes(
                    cliente.getId(), primerDiaMesAnterior.getYear(), primerDiaMesAnterior.getMonthValue(), EstadoCarrito.COMPLETADO);

            if (comprasMesAnterior == 0) {
                cliente.setEsVip(false);
                clienteRepository.save(cliente);
            }
        }

        // 1) Evalua el tipo de carrito
        TipoCarrito tipo = determinarTipo(cliente, fecha);

        Carrito carrito = Carrito.builder()
                .tipo(tipo)
                .state(EstadoCarrito.ABIERTO)
                .dateCreated(fecha)
                .montoBruto(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .cliente(cliente)
                .build();

        // 2) Evaluar al cliente ya que depende de sus compras: el carrito puede convertirse tipo VIP
        evaluarPromocionVip(carrito);

        return CarritoResponse.from(carritoRepository.save(carrito));
    }

    /**
     * Elimina FÍSICAMENTE un carrito que no haya sido completado.
     * Los detalles se borran en cascada (Carrito.detalles tiene CascadeType.ALL + orphanRemoval).
     */
    @Transactional
    public void eliminarCarrito(Long carritoId) {
        Carrito carrito = findCarritoAbierto(carritoId);
        carritoRepository.delete(carrito);
    }

    /**
     * Adds a product to the cart (or increases quantity if already present).
     */
    @Transactional
    public CarritoResponse agregarProducto(Long carritoId, AgregarProductoRequest request) {
        Carrito carrito = findCarritoAbierto(carritoId);
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + request.getProductoId()));

        carritoDetalleRepository.findByCarritoIdAndProductoId(carritoId, producto.getId())
                .ifPresentOrElse(
                        detalle -> detalle.setCantidad(detalle.getCantidad() + request.getCantidad()),
                        () -> carrito.getDetalles().add(
                                CarritoDetalle.builder()
                                        .carrito(carrito)
                                        .producto(producto)
                                        .cantidad(request.getCantidad())
                                        .precioUnitario(producto.getPrecio())
                                        .build()
                        )
                );

        recalcularTotales(carrito);
        return CarritoResponse.from(carritoRepository.save(carrito));
    }

    /**
     * Removes a product from the cart entirely.
     */
    @Transactional
    public CarritoResponse eliminarProducto(Long carritoId, Long productoId) {
        Carrito carrito = findCarritoAbierto(carritoId);

        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoIdAndProductoId(carritoId, productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en el carrito"));

        carrito.getDetalles().remove(detalle);
        recalcularTotales(carrito);
        return CarritoResponse.from(carritoRepository.save(carrito));
    }

    /**
     * Reduces the quantity of a product in the cart by a given amount.
     * If resulting quantity <= 0, removes the product entirely.
     */
    @Transactional
    public CarritoResponse reducirCantidadProducto(Long carritoId, Long productoId, int cantidad) {
        Carrito carrito = findCarritoAbierto(carritoId);

        CarritoDetalle detalle = carritoDetalleRepository.findByCarritoIdAndProductoId(carritoId, productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en el carrito"));

        int nuevaCantidad = detalle.getCantidad() - cantidad;
        if (nuevaCantidad <= 0) {
            carrito.getDetalles().remove(detalle);
        } else {
            detalle.setCantidad(nuevaCantidad);
        }

        recalcularTotales(carrito);
        return CarritoResponse.from(carritoRepository.save(carrito));
    }

    /**
     * Returns the current status and totals of a cart.
     */
    @Transactional(readOnly = true)
    public CarritoResponse consultarCarrito(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado con id: " + carritoId));
        return CarritoResponse.from(carrito);
    }

    /**
     * Búsqueda paginada con filtros opcionales (todos pueden ser null).
     * No devuelve carritos DESTRUIDO (filtro en query).
     */
    @Transactional(readOnly = true)
    public PageResponse<CarritoResponse> buscarCarritos(
            Long carritoId, Long clienteId, Integer anio, Integer mes, Pageable pageable) {
        Page<CarritoResponse> page = carritoRepository
                .buscarCarritos(carritoId, clienteId, anio, mes, pageable)
                .map(CarritoResponse::from);
        return PageResponse.from(page);
    }

    /**
     * Completes the purchase:
     * 1. Marks the cart as COMPLETADO.
     * 2. Registers the client if not already registered.
     * 3. Checks if the client spent more than 10,000 this month → promotes to VIP.
     */
    @Transactional
    public CarritoResponse finalizarCompra(Long carritoId) {
        Carrito carrito = findCarritoAbierto(carritoId);

        if (carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("No se puede finalizar un carrito vacío");
        }

        carrito.setState(EstadoCarrito.COMPLETADO);
        carritoRepository.save(carrito);

        // Check VIP promotion
        evaluarPromocionVip(carrito);

        return CarritoResponse.from(carrito);
    }

    // ─── Private helpers ────────────────────────────────────────────────────────

    private Carrito findCarritoAbierto(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado con id: " + carritoId));
        if (carrito.getState() != EstadoCarrito.ABIERTO) {
            throw new IllegalStateException("El carrito no está en estado ABIERTO (estado actual: " + carrito.getState() + ")");
        }
        return carrito;
    }

    private TipoCarrito determinarTipo(Cliente cliente, LocalDate fecha) {
        if (cliente.isEsVip()) {
            return TipoCarrito.VIP;
        }
        if (fechaEspecialRepository.existsByFecha(fecha)) {
            return TipoCarrito.FECHA_ESPECIAL;
        }
        return TipoCarrito.COMUN;
    }

    private void recalcularTotales(Carrito carrito) {
        List<CarritoDetalle> detalles = carrito.getDetalles();

        BigDecimal montoBruto = detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = getStrategy(carrito.getTipo()).calcularTotal(montoBruto, detalles);

        carrito.setMontoBruto(montoBruto);
        carrito.setTotal(total);
    }

    private DescuentoStrategy getStrategy(TipoCarrito tipo) {
        return switch (tipo) {
            case COMUN -> comunStrategy;
            case FECHA_ESPECIAL -> fechaEspecialStrategy;
            case VIP -> vipStrategy;
        };
    }

    private void evaluarPromocionVip(Carrito carrito) {
        Cliente cliente = carrito.getCliente();
        int anio = carrito.getDateCreated().getYear();
        int mes = carrito.getDateCreated().getMonthValue();

        BigDecimal totalMes = carritoRepository.sumTotalCompletadoByClienteAndMes(cliente.getId(), anio, mes, EstadoCarrito.COMPLETADO);

        if (totalMes.compareTo(Constants.VIP_THRESHOLD) > 0 && !cliente.isEsVip()) {
            cliente.setEsVip(true);
            carrito.setTipo(TipoCarrito.VIP);
            clienteRepository.save(cliente);
        }
    }
}
