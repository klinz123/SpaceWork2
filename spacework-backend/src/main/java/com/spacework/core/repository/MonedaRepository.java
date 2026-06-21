package com.spacework.core.repository;

import com.spacework.core.model.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonedaRepository extends JpaRepository<Moneda, Integer> {
    List<Moneda> findByEstadoTrue();
    Optional<Moneda> findByCodigoAndEstadoTrue(String codigo);
}
