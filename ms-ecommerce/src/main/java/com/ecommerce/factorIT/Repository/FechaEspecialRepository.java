package com.ecommerce.factorIT.Repository;

import com.ecommerce.factorIT.Model.FechaEspecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FechaEspecialRepository extends JpaRepository<FechaEspecial, Long> {

    Optional<FechaEspecial> findByFecha(LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);
}
