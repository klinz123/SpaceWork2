package com.spacework.auditoria.aop;

import com.spacework.auditoria.service.AuditoriaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;

    @Autowired
    public AuditoriaAspect(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void registrarAuditoria(JoinPoint joinPoint, Auditable auditable, Object result) {
        String accion = auditable.accion();
        String entidad = auditable.entidad();
        
        // Obtener los argumentos del método para enriquecer los detalles
        Object[] args = joinPoint.getArgs();
        String detalles = "Método: " + joinPoint.getSignature().getName();
        if (args != null && args.length > 0) {
            detalles += " | Argumentos: " + Arrays.toString(args);
        }

        auditoriaService.registrarAuditoria(accion, entidad, detalles);
    }
}
