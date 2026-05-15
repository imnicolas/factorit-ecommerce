package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Enum.TipoCarrito;
import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for VIP cart:
 * - Exactamente 4 productos: 25% off
 * - Más de 10 productos: producto más barato gratis + -$100 (base) -$500 (VIP) = -$600 + barato
 */
@Component
public class CarritoVipStrategy implements DescuentoStrategy {

    private static final BigDecimal DESCUENTO_VOLUMEN = BigDecimal.valueOf(600); // 100 + 500

    @Override
    public TipoCarrito getTipo() {
        return TipoCarrito.VIP;
    }

    @Override
    public BigDecimal calcularTotal(BigDecimal montoBruto, List<CarritoDetalle> detalles) {
        int total = totalProductos(detalles);

        if (total == CANT_PARA_25_OFF) {
            return montoBruto.multiply(DESCUENTO_4_PRODUCTOS);
        }
        if (total > CANT_PARA_VOLUMEN) {
            BigDecimal masBarato = detalles.stream()
                    .map(CarritoDetalle::getPrecioUnitario)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            return montoBruto
                    .subtract(masBarato)
                    .subtract(DESCUENTO_VOLUMEN)
                    .max(BigDecimal.ZERO);
        }
        return montoBruto;
    }
}
