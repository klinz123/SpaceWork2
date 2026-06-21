package com.spacework.reservations.repository;

import com.spacework.reservations.model.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {
    List<TipoEspacio> findByEstadoTrue();
}
