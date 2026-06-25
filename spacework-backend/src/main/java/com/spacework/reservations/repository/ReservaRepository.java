package com.spacework.reservations.repository;

import com.spacework.reservations.model.Espacio;
import com.spacework.reservations.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByUsuarioId(Integer usuarioId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reserva r WHERE r.espacio = :espacio " +
           "AND r.fechaInicioReserva < :fechaFin " +
           "AND r.fechaFinReserva > :fechaInicio " +
           "AND r.estadoReserva.nombreEstado NOT LIKE 'CANCELADA%'")
    List<Reserva> findOverlappingReservations(
            @Param("espacio") Espacio espacio,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    List<Reserva> findByEstadoReservaNombreEstadoAndFechaReservaBefore(String nombreEstado, LocalDateTime fecha);
}
