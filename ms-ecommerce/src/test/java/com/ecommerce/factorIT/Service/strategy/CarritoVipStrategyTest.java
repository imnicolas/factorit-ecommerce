package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarritoVipStrategyTest {

    private final CarritoVipStrategy strategy = new CarritoVipStrategy();

    @Test
    void conExactamente4ProductosAplicaDescuento25() {
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(500), 4)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(2000), detalles);

        // 2000 * 0.75 = 1500
        assertThat(total).isEqualByComparingTo("1500.00");
    }

    @Test
    void conMasDe10ProductosRestaProductoMasBaratoY600() {
        // 6 unidades de $200 + 6 unidades de $100 → 11+ ítems, bruto = 1200 + 600 = 1800
        // Producto más barato: $100. Total = 1800 - 100 - 600 = 1100
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(200), 6),
                detalle(BigDecimal.valueOf(100), 6)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(1800), detalles);

        assertThat(total).isEqualByComparingTo("1100");
    }

    @Test
    void conMasDe10ProductosNuncaDevuelveNegativo() {
        // Bruto muy bajo: el descuento no debería llevar a negativo
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(10), 11)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(110), detalles);

        assertThat(total).isEqualByComparingTo("0");
    }

    private CarritoDetalle detalle(BigDecimal precio, int cantidad) {
        return CarritoDetalle.builder()
                .precioUnitario(precio)
                .cantidad(cantidad)
                .build();
    }
}
