package com.spacework.crm.repository;

import com.spacework.crm.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    List<Empresa> findByEstadoTrue();
    Optional<Empresa> findByDocumentoFiscalAndEstadoTrue(String documentoFiscal);
}
