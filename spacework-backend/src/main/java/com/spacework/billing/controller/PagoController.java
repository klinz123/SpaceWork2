package com.spacework.billing.controller;

import com.spacework.billing.model.Pago;
import com.spacework.billing.repository.PagoRepository;
import com.spacework.reservations.repository.PrecioRepository;
import com.spacework.billing.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")

public class PagoController {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final PrecioRepository precioRepository;

    @Autowired
    public PagoController(PagoService pagoService, PagoRepository pagoRepository, PrecioRepository precioRepository) {
        this.pagoService = pagoService;
        this.pagoRepository = pagoRepository;
        this.precioRepository = precioRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Pago>> listarTodos() {
        List<Pago> pagos = pagoRepository.findAll();
        // enriquecer el espacio de cada reserva asociada con su precio
        for (Pago p : pagos) {
            if (p.getReserva() != null && p.getReserva().getEspacio() != null) {
                precioRepository.findFirstByEspacioIdAndEstadoTrue(p.getReserva().getEspacio().getId())
                        .ifPresent(precio -> p.getReserva().getEspacio().setPrecio(precio.getMonto()));
            }
        }
        return ResponseEntity.ok(pagos);
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> body) {
        try {
            Object reservaIdObj = body.get("reservaId");
            Integer reservaId = reservaIdObj != null ? ((Number) reservaIdObj).intValue() : null;
            Object formaPagoIdObj = body.get("formaPagoId");
            Integer formaPagoId = formaPagoIdObj != null ? ((Number) formaPagoIdObj).intValue() : null;
            Object montoValObj = body.get("montoPago");
            Double montoVal = montoValObj != null ? ((Number) montoValObj).doubleValue() : null;
            String referencia = (String) body.get("referenciaTransaccion");
            String datosTarjeta = (String) body.get("datosTarjeta");

            if (reservaId == null || formaPagoId == null || montoVal == null) {
                throw new IllegalArgumentException("Faltan parámetros obligatorios de pago.");
            }

            BigDecimal montoPago = BigDecimal.valueOf(montoVal);
            Pago pago = pagoService.procesarPago(reservaId, formaPagoId, montoPago, referencia, datosTarjeta);
            return ResponseEntity.status(HttpStatus.CREATED).body(pago);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarPago(@PathVariable Integer id) {
        try {
            Pago pago = pagoService.aprobarPago(id);
            return ResponseEntity.ok(pago);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
