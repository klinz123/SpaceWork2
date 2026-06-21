package com.spacework.reservations.repository;

import com.spacework.reservations.model.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Integer> {
    List<Espacio> findByEstadoTrue();
    List<Espacio> findByTipoEspacioIdAndEstadoTrue(Integer tipoEspacioId);
}
