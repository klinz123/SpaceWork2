package com.spacework.reservations.repository;

import com.spacework.reservations.model.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Integer> {
    Optional<Precio> findFirstByEspacioIdAndEstadoTrue(Integer espacioId);
    List<Precio> findByEspacioIdInAndEstadoTrue(java.util.List<Integer> espacioIds);
}
