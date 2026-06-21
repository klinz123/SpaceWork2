package com.spacework.billing.repository;

import com.spacework.billing.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByEstadoTrue();
    List<Factura> findByReservaIdAndEstadoTrue(Integer reservaId);
    
    // Para autogenerar número SUNAT
    Factura findTopByNumeroFacturaStartingWithOrderByNumeroFacturaDesc(String prefix);
}
