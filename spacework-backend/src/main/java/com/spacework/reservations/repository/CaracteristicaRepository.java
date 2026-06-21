package com.spacework.reservations.repository;

import com.spacework.reservations.model.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {
    List<Caracteristica> findByEstadoTrue();
}
