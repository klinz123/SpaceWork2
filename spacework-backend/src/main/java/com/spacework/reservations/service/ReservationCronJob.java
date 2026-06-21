package com.spacework.reservations.service;

import com.spacework.reservations.model.EstadoReserva;
import com.spacework.reservations.model.Reserva;
import com.spacework.reservations.repository.EstadoReservaRepository;
import com.spacework.reservations.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationCronJob {

    private final ReservaRepository reservaRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final EmailService emailService;

    @Autowired
    public ReservationCronJob(ReservaRepository reservaRepository, EstadoReservaRepository estadoReservaRepository, EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.emailService = emailService;
    }

    // Se ejecuta cada 5 minutos
    @Scheduled(fixedRate = 300000)
    public void cancelarReservasPendientesExpiradas() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        
        List<Reserva> expiradas = reservaRepository.findByEstadoReservaNombreEstadoAndFechaReservaBefore("PENDIENTE", cutoff);
        
        if (!expiradas.isEmpty()) {
            EstadoReserva estadoCancelado = estadoReservaRepository.findByNombreEstado("CANCELADO").orElse(null);
            if (estadoCancelado == null) {
                estadoCancelado = estadoReservaRepository.findByNombreEstado("CANCELADA").orElse(null);
            }
            
            if (estadoCancelado != null) {
                for (Reserva r : expiradas) {
                    r.setEstadoReserva(estadoCancelado);
                    reservaRepository.save(r);
                    
                    // Notificar al usuario opcionalmente
                    String body = "Hola " + r.getUsuario().getNombre() + ",\n\nTu reserva " + r.getCodigoReserva() + 
                                  " ha sido cancelada por el sistema debido a la falta de confirmación de pago en el tiempo límite (2 horas).\n\n" +
                                  "Si deseas, puedes volver a realizar una nueva reserva en nuestra plataforma.";
                    emailService.sendEmail(r.getUsuario().getCorreoElectronico(), "Reserva Cancelada por Tiempo de Espera - SpaceWork", body);
                }
                System.out.println("CronJob: Se han cancelado " + expiradas.size() + " reservas por falta de pago.");
            }
        }
    }
}
