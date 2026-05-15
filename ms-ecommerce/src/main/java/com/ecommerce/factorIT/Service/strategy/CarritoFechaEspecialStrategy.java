package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for FECHA_ESPECIAL cart:
 * - Exactamente 4 productos: 25% off
 * - Más de 10 productos: -$100 (base) -$300 (fecha especial) = -$400
 */
@Component
public class CarritoFechaEspecialStrategy implements DescuentoStrategy {

    private static final BigDecimal DESCUENTO_VOLUMEN = BigDecimal.valueOf(400); // 100 + 300

    @Override
    public TipoCarrito getTipo() {
        return TipoCarrito.FECHA_ESPECIAL;
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
