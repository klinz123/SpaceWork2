package com.spacework.core.repository;

import com.spacework.core.model.Impuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, Integer> {
    List<Impuesto> findByEstadoTrue();
    Optional<Impuesto> findByNombreAndEstadoTrue(String nombre);
}
