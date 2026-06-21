package com.spacework.auditoria.repository;

import com.spacework.auditoria.model.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Integer> {
}
