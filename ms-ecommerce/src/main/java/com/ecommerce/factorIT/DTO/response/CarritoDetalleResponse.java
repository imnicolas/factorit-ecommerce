package com.ecommerce.factorIT.DTO.response;

import com.ecommerce.factorIT.Model.CarritoDetalle;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CarritoDetalleResponse {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public static CarritoDetalleResponse from(CarritoDetalle detalle) {
        return CarritoDetalleResponse.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoNombre(detalle.getProducto().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .build();
    }
}
