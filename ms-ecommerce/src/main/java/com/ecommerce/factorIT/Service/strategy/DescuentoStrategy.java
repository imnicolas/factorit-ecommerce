package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.CarritoDetalle;

import java.math.BigDecimal;
import java.util.List;

public interface DescuentoStrategy {

    /** Factor multiplicativo cuando hay exactamente 4 productos (25% off). */
    BigDecimal DESCUENTO_4_PRODUCTOS = BigDecimal.valueOf(0.75);

    /** Cantidad exacta de productos que activa el 25% off. */
    int CANT_PARA_25_OFF = 4;

    /** Mínimo de productos para activar los descuentos por volumen. */
    int CANT_PARA_VOLUMEN = 10;

    TipoCarrito getTipo();

    BigDecimal calcularTotal(BigDecimal montoBruto, List<CarritoDetalle> detalles);

    default int totalProductos(List<CarritoDetalle> detalles) {
        return detalles.stream().mapToInt(CarritoDetalle::getCantidad).sum();
    }
}
