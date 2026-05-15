package com.ecommerce.factorIT.DTO.response;

import com.ecommerce.factorIT.Model.Producto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductoResponse {

    private Long id;
    private String nombre;
    private BigDecimal precio;

    public static ProductoResponse from(Producto producto) {
        return ProductoResponse.builder().
                id(producto.getId()).
                nombre(producto.getNombre()).
                precio(producto.getPrecio()).
                build();
    }
}
