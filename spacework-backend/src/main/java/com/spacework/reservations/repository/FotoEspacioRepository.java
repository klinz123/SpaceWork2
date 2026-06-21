package com.spacework.reservations.repository;

import com.spacework.reservations.model.FotoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoEspacioRepository extends JpaRepository<FotoEspacio, Integer> {
    List<FotoEspacio> findByEspacioId(Integer espacioId);
}
