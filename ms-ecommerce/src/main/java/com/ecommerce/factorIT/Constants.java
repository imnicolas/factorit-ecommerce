package com.ecommerce.factorIT;

import java.math.BigDecimal;

/**
 * Constantes globales del dominio.
 * Centraliza valores que antes estaban duplicados entre Services.
 */
public final class Constants {

    /** Umbral mensual de compras por encima del cual un cliente es VIP. */
    public static final BigDecimal VIP_THRESHOLD = BigDecimal.valueOf(10_000);

    private Constants() {
        // utility class
    }
}
