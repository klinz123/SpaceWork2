package com.spacework.reservations.repository;

import com.spacework.reservations.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    List<Ubicacion> findByEstadoTrue();
}
