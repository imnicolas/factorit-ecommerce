package com.ecommerce.factorIT.Service;

import com.ecommerce.factorIT.DTO.request.CrearCarritoRequest;
import com.ecommerce.factorIT.DTO.response.CarritoResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private CarritoDetalleRepository carritoDetalleRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private FechaEspecialRepository fechaEspecialRepository;
    @Mock private CarritoComunStrategy comunStrategy;
    @Mock private CarritoFechaEspecialStrategy fechaEspecialStrategy;
    @Mock private CarritoVipStrategy vipStrategy;

    @InjectMocks
    private CarritoService carritoService;

    private Cliente clienteComun;

    @BeforeEach
    void setUp() {
        clienteComun = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .dni("12345678")
                .esVip(false)
                .build();
    }

    @Test
    void crearCarritoConClienteComunYFechaNormalDevuelveTipoComun() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteComun));
        when(fechaEspecialRepository.existsByFecha(any())).thenReturn(false);
        when(carritoRepository.sumTotalCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponse response = carritoService.crearCarrito(
                requestPara(1L), LocalDate.of(2026, 5, 14));

        assertThat(response.getTipo()).isEqualTo(TipoCarrito.COMUN);
    }

    @Test
    void crearCarritoConFechaEspecialDevuelveTipoFechaEspecial() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteComun));
        when(fechaEspecialRepository.existsByFecha(LocalDate.of(2026, 12, 25))).thenReturn(true);
        when(carritoRepository.sumTotalCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponse response = carritoService.crearCarrito(
                requestPara(1L), LocalDate.of(2026, 12, 25));

        assertThat(response.getTipo()).isEqualTo(TipoCarrito.FECHA_ESPECIAL);
    }

    @Test
    void crearCarritoConClienteVipDevuelveTipoVip() {
        Cliente vip = Cliente.builder().id(2L).nombre("María").apellido("Gómez")
                .dni("87654321").esVip(true).build();

        when(clienteRepository.findById(2L)).thenReturn(Optional.of(vip));
        when(carritoRepository.countCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(3L); // sí compró el mes pasado, mantiene VIP
        when(carritoRepository.sumTotalCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponse response = carritoService.crearCarrito(
                requestPara(2L), LocalDate.of(2026, 5, 14));

        assertThat(response.getTipo()).isEqualTo(TipoCarrito.VIP);
    }

    @Test
    void clienteVipSinComprasElMesAnteriorPierdeElEstadoVip() {
        Cliente vip = Cliente.builder().id(2L).nombre("María").apellido("Gómez")
                .dni("87654321").esVip(true).build();

        when(clienteRepository.findById(2L)).thenReturn(Optional.of(vip));
        when(carritoRepository.countCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(0L); // NO compró el mes pasado → pierde VIP
        when(fechaEspecialRepository.existsByFecha(any())).thenReturn(false);
        when(carritoRepository.sumTotalCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponse response = carritoService.crearCarrito(
                requestPara(2L), LocalDate.of(2026, 5, 14));

        assertThat(vip.isEsVip()).isFalse();
        assertThat(response.getTipo()).isEqualTo(TipoCarrito.COMUN);
        verify(clienteRepository).save(vip);
    }

    @Test
    void finalizarCompraConTotalSuperiorA10000PromueveAVip() {
        Carrito carrito = carritoAbiertoConDetalles(clienteComun);

        when(carritoRepository.findById(99L)).thenReturn(Optional.of(carrito));
        when(carritoRepository.sumTotalCompletadoByClienteAndMes(anyLong(), anyInt(), anyInt(), any()))
                .thenReturn(BigDecimal.valueOf(15000));

        carritoService.finalizarCompra(99L);

        assertThat(carrito.getState()).isEqualTo(EstadoCarrito.COMPLETADO);
        assertThat(clienteComun.isEsVip()).isTrue();
        verify(clienteRepository).save(clienteComun);
    }

    @Test
    void finalizarCompraDeCarritoVacioLanzaError() {
        Carrito vacio = Carrito.builder()
                .id(99L).cliente(clienteComun).state(EstadoCarrito.ABIERTO)
                .tipo(TipoCarrito.COMUN).dateCreated(LocalDate.now())
                .montoBruto(BigDecimal.ZERO).total(BigDecimal.ZERO)
                .detalles(new ArrayList<>())
                .build();

        when(carritoRepository.findById(99L)).thenReturn(Optional.of(vacio));

        assertThatThrownBy(() -> carritoService.finalizarCompra(99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vacío");

        verify(carritoRepository, never()).save(any());
    }

    @Test
    void eliminarCarritoLoDejaEnEstadoDestruido() {
        Carrito carrito = Carrito.builder()
                .id(50L).cliente(clienteComun).state(EstadoCarrito.ABIERTO)
                .tipo(TipoCarrito.COMUN).dateCreated(LocalDate.now())
                .detalles(new ArrayList<>())
                .build();

        when(carritoRepository.findById(50L)).thenReturn(Optional.of(carrito));

        carritoService.eliminarCarrito(50L);

        assertThat(carrito.getState()).isEqualTo(EstadoCarrito.DESTRUIDO);
        verify(carritoRepository).save(carrito);
    }

    // ─── helpers ────────────────────────────────────────────────────────

    private CrearCarritoRequest requestPara(Long clienteId) {
        CrearCarritoRequest req = new CrearCarritoRequest();
        req.setClienteId(clienteId);
        return req;
    }

    private Carrito carritoAbiertoConDetalles(Cliente cliente) {
        Producto p = Producto.builder().id(1L).nombre("Laptop").precio(BigDecimal.valueOf(5000)).build();
        CarritoDetalle d = CarritoDetalle.builder()
                .producto(p).cantidad(3).precioUnitario(p.getPrecio()).build();

        ArrayList<CarritoDetalle> detalles = new ArrayList<>();
        detalles.add(d);

        return Carrito.builder()
                .id(99L).cliente(cliente).state(EstadoCarrito.ABIERTO)
                .tipo(TipoCarrito.COMUN).dateCreated(LocalDate.of(2026, 5, 14))
                .montoBruto(BigDecimal.valueOf(15000))
                .total(BigDecimal.valueOf(15000))
                .detalles(detalles)
                .build();
    }
}
