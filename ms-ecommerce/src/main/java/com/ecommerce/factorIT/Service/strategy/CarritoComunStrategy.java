package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for COMUN cart:
 * - Exactamente 4 productos: 25% off
 * - Más de 10 productos: -$100
 */
@Component
public class CarritoComunStrategy implements DescuentoStrategy {

    private static final BigDecimal DESCUENTO_VOLUMEN = BigDecimal.valueOf(100);

    @Override
    public TipoCarrito getTipo() {
        return TipoCarrito.COMUN;
    }

    @Override
    public BigDecimal calcularTotal(BigDecimal montoBruto, List<CarritoDetalle> detalles) {
        int total = totalProductos(detalles);

        if (total == CANT_PARA_25_OFF) {
            return montoBruto.multiply(DESCUENTO_4_PRODUCTOS);
        }
        if (total > CANT_PARA_VOLUMEN) {
            return montoBruto.subtract(DESCUENTO_VOLUMEN).max(BigDecimal.ZERO);
        }
        return montoBruto;
    }
}
