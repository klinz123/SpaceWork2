package com.spacework.reservations.repository;

import com.spacework.reservations.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Integer> {

    List<Resena> findByEspacioIdAndEstadoTrueOrderByFechaResenaDesc(Integer espacioId);

    List<Resena> findByReservaId(Integer reservaId);

    List<Resena> findByUsuarioIdOrderByFechaResenaDesc(Integer usuarioId);

    List<Resena> findAllByOrderByFechaResenaDesc();

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.espacio.id = :espacioId AND r.estado = true")
    Double obtenerPromedioPorEspacio(@Param("espacioId") Integer espacioId);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.reserva.id = :reservaId")
    long countByReservaId(@Param("reservaId") Integer reservaId);
}
