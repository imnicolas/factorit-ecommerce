package com.ecommerce.factorIT.Controller;

import com.ecommerce.factorIT.DTO.request.AgregarProductoRequest;
import com.ecommerce.factorIT.DTO.request.CrearCarritoRequest;
import com.ecommerce.factorIT.DTO.response.CarritoResponse;
import com.ecommerce.factorIT.DTO.response.PageResponse;
import com.ecommerce.factorIT.Service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Carritos", description = "Gestión de carritos de compras")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    @Operation(
            summary = "Buscar carritos paginado",
            description = "Devuelve carritos ordenados por id desc. Filtros opcionales: " +
                    "carritoId, clienteId, anio, mes. Los carritos DESTRUIDO no se devuelven."
    )
    public ResponseEntity<PageResponse<CarritoResponse>> buscarCarritos(
            @Parameter(description = "ID de carrito exacto")
            @RequestParam(required = false) Long carritoId,
            @Parameter(description = "ID del cliente")
            @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Año del carrito (ej: 2026)")
            @RequestParam(required = false) Integer anio,
            @Parameter(description = "Mes del carrito (1-12)")
            @RequestParam(required = false) @Min(1) @Max(12) Integer mes,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        log.info("[GET] Buscando carritos carritoId={} clienteId={} anio={} mes={} page={} size={}",
                carritoId, clienteId, anio, mes, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(carritoService.buscarCarritos(carritoId, clienteId, anio, mes, pageable));
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo carrito",
            description = "Crea un carrito para el cliente. El tipo (COMUN, FECHA_ESPECIAL, VIP) se determina automáticamente. "
                    + "Se puede simular una fecha con el parámetro fechaSimulada."
    )
    public ResponseEntity<CarritoResponse> crearCarrito(
            @Valid @RequestBody CrearCarritoRequest request,
            @Parameter(description = "Fecha simulada para la creación del carrito (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSimulada
    ) {
        log.info("[POST] Creando carrito para cliente con ID {}", request.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.crearCarrito(request, fechaSimulada));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un carrito",
            description = "Destruye un carrito que no haya sido finalizado. El carrito pasa a estado DESTRUIDO."
    )
    public ResponseEntity<Void> eliminarCarrito(@PathVariable Long id) {
        log.info("[DELETE] Eliminando carrito con ID {}", id);
        carritoService.eliminarCarrito(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar estado del carrito",
            description = "Devuelve los ítems, monto bruto, descuentos aplicados y total a pagar."
    )
    public ResponseEntity<CarritoResponse> consultarCarrito(@PathVariable Long id) {
        log.info("[GET] Consultando estado del carrito con ID {}", id);
        return ResponseEntity.ok(carritoService.consultarCarrito(id));
    }

    @PostMapping("/{id}/productos")
    @Operation(
            summary = "Agregar producto al carrito",
            description = "Agrega un producto con la cantidad indicada. Si ya existe en el carrito, incrementa la cantidad."
    )
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable Long id,
            @Valid @RequestBody AgregarProductoRequest request
    ) {
        log.info("[POST] Agregando producto {} al carrito con ID {}", request.getProductoId(), id);
        return ResponseEntity.ok(carritoService.agregarProducto(id, request));
    }

    @DeleteMapping("/{id}/productos/{productoId}")
    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un producto del carrito. Si se indica 'cantidad', solo reduce ese número de unidades; "
                    + "de lo contrario elimina el producto completamente."
    )
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable Long id,
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad a reducir (si no se indica, se elimina el producto completo)")
            @RequestParam(required = false) Integer cantidad
    ) {
        log.info("[DELETE] Eliminando producto {} del carrito con ID {}", productoId, id);
        CarritoResponse response = (cantidad != null)
                ? carritoService.reducirCantidadProducto(id, productoId, cantidad)
                : carritoService.eliminarProducto(id, productoId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/finalizar")
    @Operation(
            summary = "Finalizar compra",
            description = "Completa la compra del carrito. El cliente queda registrado y se evalúa si pasa a ser VIP."
    )
    public ResponseEntity<CarritoResponse> finalizarCompra(@PathVariable Long id) {
        log.info("[POST] Finalizando compra del carrito con ID {}", id);
        return ResponseEntity.ok(carritoService.finalizarCompra(id));
    }
}
