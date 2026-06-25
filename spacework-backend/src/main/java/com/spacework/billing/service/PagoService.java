package com.spacework.billing.service;

import com.spacework.billing.model.Pago;
import com.spacework.reservations.model.Reserva;
import com.spacework.billing.model.FormaPago;
import com.spacework.reservations.model.EstadoReserva;
import com.spacework.billing.repository.PagoRepository;
import com.spacework.reservations.repository.ReservaRepository;
import com.spacework.billing.repository.FormaPagoRepository;
import com.spacework.reservations.repository.EstadoReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final FacturaService facturaService;

    @Autowired
    public PagoService(PagoRepository pagoRepository,
                       ReservaRepository reservaRepository,
                       FormaPagoRepository formaPagoRepository,
                       EstadoReservaRepository estadoReservaRepository,
                       FacturaService facturaService) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.facturaService = facturaService;
    }

    @Transactional
    public Pago procesarPago(Integer reservaId, Integer formaPagoId, BigDecimal monto, String referencia, String datosTarjeta) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de pago debe ser mayor a cero.");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));

        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstadoReserva().getNombreEstado()) && !"FINALIZADA".equalsIgnoreCase(reserva.getEstadoReserva().getNombreEstado())) {
            if (monto.compareTo(reserva.getMontoTotal()) < 0) {
                throw new IllegalArgumentException("El monto de pago no cubre el total de la reserva.");
            }
        }

        FormaPago formaPago = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new IllegalArgumentException("Forma de pago no encontrada."));

        boolean esPagoInmediato = formaPago.getNombreForma().toLowerCase().contains("tarjeta");

        if (esPagoInmediato) {
            // actualizamos el estado de la reserva a CONFIRMADA inmediatamente
            EstadoReserva estadoConfirmada = estadoReservaRepository.findByNombreEstado("CONFIRMADA")
                    .orElseThrow(() -> new IllegalStateException("Estado CONFIRMADA no configurado en el sistema."));
            reserva.setEstadoReserva(estadoConfirmada);
            reservaRepository.save(reserva);
            
            // generamos factura automática
            if (facturaService.obtenerTodasLasFacturas().stream().anyMatch(f -> f.getReserva().getId().equals(reserva.getId()))) {
                facturaService.generarFacturaAdicional(reserva, monto, "Servicio Adicional (Extra)");
            } else {
                facturaService.generarFacturaDeReserva(reserva);
            }
        }

        // crear el registro de pago
        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setFormaPago(formaPago);
        pago.setMontoPago(monto);
        pago.setFechaPago(LocalDateTime.now());
        pago.setReferenciaTransaccion(referencia != null ? referencia : "TXN-" + System.currentTimeMillis());
        pago.setEstadoPago(esPagoInmediato ? "PAGADO" : "PENDIENTE_VERIFICACION");
        pago.setEstado(true);

        // DatosTarjeta ya no se guarda en la base de datos por cumplimiento PCI DSS

        return pagoRepository.save(pago);
    }

    @Transactional
    public Pago aprobarPago(Integer pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado."));

        if (!"PENDIENTE_VERIFICACION".equals(pago.getEstadoPago())) {
            throw new IllegalStateException("El pago ya ha sido procesado o cancelado.");
        }

        // marcar pago como PAGADO
        pago.setEstadoPago("PAGADO");
        pagoRepository.save(pago);

        // marcar reserva vinculada como CONFIRMADA
        Reserva reserva = pago.getReserva();
        EstadoReserva estadoConfirmada = estadoReservaRepository.findByNombreEstado("CONFIRMADA")
                .orElseThrow(() -> new IllegalStateException("Estado CONFIRMADA no configurado en el sistema."));
        reserva.setEstadoReserva(estadoConfirmada);
        reservaRepository.save(reserva);
        
        // generamos factura automática
        if (facturaService.obtenerTodasLasFacturas().stream().anyMatch(f -> f.getReserva().getId().equals(reserva.getId()))) {
            facturaService.generarFacturaAdicional(reserva, pago.getMontoPago(), "Servicio Adicional (Extra)");
        } else {
            facturaService.generarFacturaDeReserva(reserva);
        }

        return pago;
    }
}
