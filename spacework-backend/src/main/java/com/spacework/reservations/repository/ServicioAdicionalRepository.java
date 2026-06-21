package com.spacework.reservations.repository;

import com.spacework.reservations.model.ServicioAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioAdicionalRepository extends JpaRepository<ServicioAdicional, Integer> {
    List<ServicioAdicional> findByEstado(boolean estado);
}
