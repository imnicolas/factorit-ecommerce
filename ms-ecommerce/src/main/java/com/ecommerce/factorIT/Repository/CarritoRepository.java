package com.ecommerce.factorIT.Repository;

import com.ecommerce.factorIT.Enum.EstadoCarrito;
import com.ecommerce.factorIT.Model.Carrito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * Sum of totals of completed carts for a client in a given month/year.
     */
    @Query("""
            SELECT COALESCE(SUM(c.total), 0) FROM Carrito c
            WHERE c.cliente.id = :clienteId
              AND c.state = :state
              AND YEAR(c.dateCreated) = :anio
              AND MONTH(c.dateCreated) = :mes
            """)
    BigDecimal sumTotalCompletadoByClienteAndMes(
            @Param("clienteId") Long clienteId,
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("state") EstadoCarrito state);

    /**
     * Check if a client made any completed purchase in a given month/year.
     */
    @Query("""
            SELECT COUNT(c) FROM Carrito c
            WHERE c.cliente.id = :clienteId
              AND c.state = :state
              AND YEAR(c.dateCreated) = :anio
              AND MONTH(c.dateCreated) = :mes
            """)
    long countCompletadoByClienteAndMes(
            @Param("clienteId") Long clienteId,
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("state") EstadoCarrito state);

    /**
     * Búsqueda paginada de carritos con filtros opcionales.
     * Excluye explícitamente los DESTRUIDO (a partir de ahora, "destruir" elimina físicamente,
     * pero la query es defensiva por si quedaron registros viejos en la DB).
     */
    @Query("""
            SELECT c FROM Carrito c
            JOIN c.cliente cl
            WHERE c.state <> com.ecommerce.factorIT.Enum.EstadoCarrito.DESTRUIDO
              AND (:carritoId IS NULL OR c.id = :carritoId)
              AND (:clienteId IS NULL OR cl.id = :clienteId)
              AND (:anio IS NULL OR YEAR(c.dateCreated) = :anio)
              AND (:mes IS NULL OR MONTH(c.dateCreated) = :mes)
            """)
    Page<Carrito> buscarCarritos(
            @Param("carritoId") Long carritoId,
            @Param("clienteId") Long clienteId,
            @Param("anio") Integer anio,
            @Param("mes") Integer mes,
            Pageable pageable);
}
