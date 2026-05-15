package com.ecommerce.factorIT.Service.strategy;

import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarritoComunStrategyTest {

    private final CarritoComunStrategy strategy = new CarritoComunStrategy();

    @Test
    void conExactamente4ProductosAplicaDescuento25() {
        // 4 unidades a $100 → bruto 400, con 25% off = 300
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(100), 4)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(400), detalles);

        assertThat(total).isEqualByComparingTo("300.00");
    }

    @Test
    void conMasDe10ProductosDescuenta100() {
        // 11 unidades a $50 → bruto 550, menos 100 = 450
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(50), 11)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(550), detalles);

        assertThat(total).isEqualByComparingTo("450");
    }

    @Test
    void con5ProductosNoAplicaNingunDescuento() {
        List<CarritoDetalle> detalles = List.of(
                detalle(BigDecimal.valueOf(100), 5)
        );

        BigDecimal total = strategy.calcularTotal(BigDecimal.valueOf(500), detalles);

        assertThat(total).isEqualByComparingTo("500");
    }

    private CarritoDetalle detalle(BigDecimal precio, int cantidad) {
        return CarritoDetalle.builder()
                .precioUnitario(precio)
                .cantidad(cantidad)
                .build();
    }
}
