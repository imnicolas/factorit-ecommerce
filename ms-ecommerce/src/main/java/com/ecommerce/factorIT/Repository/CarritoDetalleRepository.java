package com.ecommerce.factorIT.Repository;

import com.ecommerce.factorIT.Model.CarritoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Long> {

    Optional<CarritoDetalle> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
}
