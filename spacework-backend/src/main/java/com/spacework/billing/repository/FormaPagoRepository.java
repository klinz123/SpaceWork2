package com.spacework.billing.repository;

import com.spacework.billing.model.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Integer> {
    Optional<FormaPago> findByNombreForma(String nombreForma);
}
