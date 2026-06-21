package com.spacework.reservations.repository;

import com.spacework.reservations.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Integer> {
    Optional<EstadoReserva> findByNombreEstado(String nombreEstado);
}
