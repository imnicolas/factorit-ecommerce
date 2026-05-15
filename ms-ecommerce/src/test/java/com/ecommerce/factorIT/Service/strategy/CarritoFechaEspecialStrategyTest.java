package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarritoFechaEspecialStrategyTest {

    private final CarritoFechaEspecialStrategy strategy = new CarritoFechaEspecialStrategy();

    @Test
    void conExactamente4ProductosAplicaDescuento25() {
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(200), 4)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(800), detalles);

        // 800 * 0.75 = 600
        assertThat(total).isEqualByComparingTo("600.00");
    }

    @Test
    void conMasDe10ProductosDescuenta400() {
        // 11 unidades a $100 → bruto 1100, menos 400 (100 base + 300 fecha especial) = 700
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(100), 11)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(1100), detalles);

        assertThat(total).isEqualByComparingTo("700");
    }

    @Test
    void con3ProductosNoAplicaNingunDescuento() {
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(100), 3)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(300), detalles);

        assertThat(total).isEqualByComparingTo("300");
    }

    private CarritoDetalle detalle(BigDecimal precio, int cantidad) {
        return CarritoDetalle.builder()
                .precioUnitario(precio)
                .cantidad(cantidad)
                .build();
    }
}
