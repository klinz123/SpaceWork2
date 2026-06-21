package com.spacework.reservations.repository;

import com.spacework.reservations.model.ReservaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, Integer> {
    List<ReservaServicio> findByReservaId(Integer reservaId);
}
