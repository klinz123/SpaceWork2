package com.spacework.core.config;

import com.spacework.core.model.Impuesto;
import com.spacework.core.model.Moneda;
import com.spacework.core.repository.ImpuestoRepository;
import com.spacework.core.repository.MonedaRepository;
import com.spacework.crm.model.Rol;
import com.spacework.crm.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final MonedaRepository monedaRepository;
    private final ImpuestoRepository impuestoRepository;

    public DataInitializer(RolRepository rolRepository, MonedaRepository monedaRepository, ImpuestoRepository impuestoRepository) {
        this.rolRepository = rolRepository;
        this.monedaRepository = monedaRepository;
        this.impuestoRepository = impuestoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Init Roles
        initRole("ROLE_SUPERADMIN", "Acceso total al sistema ERP");
        initRole("ROLE_ADMINISTRADOR", "Administrador de la sede");
        initRole("ROLE_CLIENTE", "Cliente que realiza reservas");

        // Init Monedas
        initMoneda("PEN", "Soles", "S/");
        initMoneda("USD", "Dolares", "$");

        // Init Impuestos
        initImpuesto("IGV", new BigDecimal("18.00"));
    }

    private void initRole(String nombre, String descripcion) {
        // En Spring Security los roles a menudo tienen el prefijo ROLE_ (aunque hasAnyRole lo omite)
        // Guardaremos sin el prefijo si tu JwtFilter o config asume eso, 
        // pero por convención usaremos nombres exactos.
        String nombreSinPrefijo = nombre.replace("ROLE_", "");
        if (rolRepository.findByNombreRol(nombreSinPrefijo).isEmpty()) {
            rolRepository.save(new Rol(nombreSinPrefijo, descripcion));
        }
    }

    private void initMoneda(String codigo, String nombre, String simbolo) {
        if (monedaRepository.findByCodigoAndEstadoTrue(codigo).isEmpty()) {
            Moneda m = new Moneda();
            m.setCodigo(codigo);
            m.setNombre(nombre);
            m.setSimbolo(simbolo);
            monedaRepository.save(m);
        }
    }

    private void initImpuesto(String nombre, BigDecimal porcentaje) {
        if (impuestoRepository.findByNombreAndEstadoTrue(nombre).isEmpty()) {
            Impuesto imp = new Impuesto();
            imp.setNombre(nombre);
            imp.setPorcentaje(porcentaje);
            impuestoRepository.save(imp);
        }
    }
}
