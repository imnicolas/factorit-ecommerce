package com.ecommerce.factorIT.Repository;

import com.ecommerce.factorIT.Model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Búsqueda paginada por nombre, apellido o dni (LIKE %search%, case-insensitive).
     */
    @Query("""
            SELECT c FROM Cliente c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :search, '%'))
               OR c.dni LIKE CONCAT('%', :search, '%')
            """)
    Page<Cliente> buscarPaginado(@Param("search") String search, Pageable pageable);

    /**
     * VIPs en mes/año dado: clientes que sumaron > threshold en COMPLETADOS de ese mes.
     * Es la fuente de verdad: ser VIP en un mes = haber comprado > $10.000 ese mes.
     */
    @Query(value = """
            SELECT c FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """,
            countQuery = """
            SELECT COUNT(c) FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """)
    Page<Cliente> findVipsEnMes(
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("threshold") java.math.BigDecimal threshold,
            Pageable pageable);

    /**
     * Ganaron VIP en mes X: son VIP en X pero NO eran VIP en (X-1).
     */
    @Query(value = """
            SELECT c FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            AND c.id NOT IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anioAnt
                  AND MONTH(cr.dateCreated) = :mesAnt
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """,
            countQuery = """
            SELECT COUNT(c) FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            AND c.id NOT IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anioAnt
                  AND MONTH(cr.dateCreated) = :mesAnt
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """)
    Page<Cliente> findNuevosVipEnMes(
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("anioAnt") int anioAnt,
            @Param("mesAnt") int mesAnt,
            @Param("threshold") java.math.BigDecimal threshold,
            Pageable pageable);

    /**
     * Perdieron VIP en mes X: eran VIP en (X-1) pero NO son VIP en X.
     */
    @Query(value = """
            SELECT c FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anioAnt
                  AND MONTH(cr.dateCreated) = :mesAnt
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            AND c.id NOT IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """,
            countQuery = """
            SELECT COUNT(c) FROM Cliente c
            WHERE c.id IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anioAnt
                  AND MONTH(cr.dateCreated) = :mesAnt
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            AND c.id NOT IN (
                SELECT cr.cliente.id FROM Carrito cr
                WHERE cr.state = com.ecommerce.factorIT.Enum.EstadoCarrito.COMPLETADO
                  AND YEAR(cr.dateCreated) = :anio
                  AND MONTH(cr.dateCreated) = :mes
                GROUP BY cr.cliente.id
                HAVING SUM(cr.total) > :threshold
            )
            """)
    Page<Cliente> findPerdieronVipEnMes(
            @Param("anio") int anio,
            @Param("mes") int mes,
            @Param("anioAnt") int anioAnt,
            @Param("mesAnt") int mesAnt,
            @Param("threshold") java.math.BigDecimal threshold,
            Pageable pageable);
}
