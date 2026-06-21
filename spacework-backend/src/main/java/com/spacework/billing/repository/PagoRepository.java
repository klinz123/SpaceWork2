package com.spacework.billing.repository;

import com.spacework.billing.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByReservaId(Integer reservaId);
}
