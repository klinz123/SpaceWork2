package com.spacework.reservations.service;

import com.spacework.reservations.model.Espacio;
import com.spacework.reservations.model.EstadoReserva;
import com.spacework.reservations.model.Reserva;
import com.spacework.crm.model.Usuario;
import com.spacework.reservations.repository.EstadoReservaRepository;
import com.spacework.reservations.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.spacework.reservations.model.ServicioAdicional;
import com.spacework.reservations.model.ReservaServicio;
import com.spacework.reservations.repository.ServicioAdicionalRepository;
import com.spacework.reservations.repository.ReservaServicioRepository;
import com.spacework.auditoria.aop.Auditable;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final EmailService emailService;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ReservaServicioRepository reservaServicioRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, 
                          EstadoReservaRepository estadoReservaRepository,
                          EmailService emailService,
                          ServicioAdicionalRepository servicioAdicionalRepository,
                          ReservaServicioRepository reservaServicioRepository) {
        this.reservaRepository = reservaRepository;
        this.estadoReservaRepository = estadoReservaRepository;
        this.emailService = emailService;
        this.servicioAdicionalRepository = servicioAdicionalRepository;
        this.reservaServicioRepository = reservaServicioRepository;
    }

    @Transactional
    @Auditable(accion = "CREAR_RESERVA", entidad = "Reservas")
    public Reserva crearReserva(Usuario usuario, Espacio espacio, LocalDateTime inicio, LocalDateTime fin, BigDecimal montoTotal, BigDecimal descuentoAplicado, String observaciones, Map<Integer, Integer> serviciosAdicionales, String tipoComprobante, String documentoIdentidad, String razonSocial, String direccion) {
        // Primero nos aseguramos de que no nos estén enviando datos nulos o fechas imposibles
        if (inicio.isAfter(fin) || inicio.isEqual(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La reserva no puede ser en el pasado.");
        }

        // Ojo aquí: el cliente no puede saltarse el horario de atención de la sede
        java.time.LocalTime horaInicio = inicio.toLocalTime();
        java.time.LocalTime horaFin = fin.toLocalTime();
        if (horaInicio.isBefore(espacio.getHoraApertura()) || horaFin.isAfter(espacio.getHoraCierre())) {
            throw new IllegalArgumentException(String.format("La hora de la reserva debe estar dentro del horario de atención del local (%s - %s).",
                    espacio.getHoraApertura(), espacio.getHoraCierre()));
        }

        // Regla de negocio core: Evitar doble reserva en la misma sala y a la misma hora
        List<Reserva> overlapping = reservaRepository.findOverlappingReservations(espacio, inicio, fin);
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("El espacio ya se encuentra reservado en el rango de tiempo seleccionado.");
        }

        // Buscamos el estado inicial (quemado a PENDIENTE por ahora)
        EstadoReserva estado = estadoReservaRepository.findByNombreEstado("PENDIENTE")
                .orElseThrow(() -> new IllegalStateException("Estado PENDIENTE no configurado en la base de datos."));

        // Armamos el objeto de la reserva
        Reserva reserva = new Reserva();
        reserva.setCodigoReserva("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setFechaInicioReserva(inicio);
        reserva.setFechaFinReserva(fin);
        reserva.setMontoTotal(montoTotal);
        if (descuentoAplicado != null) {
            reserva.setDescuentoAplicado(descuentoAplicado);
        } else {
            reserva.setDescuentoAplicado(BigDecimal.ZERO);
        }
        reserva.setEstadoReserva(estado);
        reserva.setObservaciones(observaciones);
        reserva.setTipoComprobante(tipoComprobante);
        reserva.setDocumentoIdentidad(documentoIdentidad);
        reserva.setRazonSocial(razonSocial);
        reserva.setDireccion(direccion);

        Reserva nuevaReserva = reservaRepository.save(reserva);

        // Si compraron sillas extras o proyector, los guardamos de una vez
        if (serviciosAdicionales != null && !serviciosAdicionales.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : serviciosAdicionales.entrySet()) {
                Integer servicioId = entry.getKey();
                Integer cantidad = entry.getValue();
                if (cantidad > 0) {
                    ServicioAdicional sa = servicioAdicionalRepository.findById(servicioId).orElse(null);
                    if (sa != null) {
                        ReservaServicio rs = new ReservaServicio();
                        rs.setReserva(nuevaReserva);
                        rs.setServicioAdicional(sa);
                        rs.setCantidad(cantidad);
                        BigDecimal subtotalServicio = sa.getPrecioBase().multiply(BigDecimal.valueOf(cantidad));
                        rs.setSubtotal(subtotalServicio);
                        reservaServicioRepository.save(rs);
                        // Send email/notification to third party logic can go here in the future
                    }
                }
            }
        }

        // Disparamos el correo de confirmación de forma síncrona (TODO: podríamos pasarlo a RabbitMQ después)
        String subject = "Confirmación de Reserva - SpaceWork (" + nuevaReserva.getCodigoReserva() + ")";
        String body = String.format(
                "Hola %s,\n\nTu reserva para el espacio '%s' ha sido registrada exitosamente.\n\n" +
                "Detalles de la Reserva:\n" +
                "- Código: %s\n" +
                "- Inicio: %s\n" +
                "- Fin: %s\n" +
                "- Monto Total: S/. %s\n" +
                "- Estado: %s\n\n" +
                "Gracias por utilizar SpaceWork.\n",
                usuario.getNombre(),
                espacio.getNombreEspacio(),
                nuevaReserva.getCodigoReserva(),
                nuevaReserva.getFechaInicioReserva(),
                nuevaReserva.getFechaFinReserva(),
                nuevaReserva.getMontoTotal(),
                nuevaReserva.getEstadoReserva().getNombreEstado()
        );
        emailService.sendEmail(usuario.getCorreoElectronico(), subject, body);

        return nuevaReserva;
    }

    @Transactional
    public List<ReservaServicio> agregarServiciosExtra(Integer reservaId, List<Map<String, Object>> serviciosReq, Usuario usuarioSolicitante) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        String rolSolicitante = usuarioSolicitante.getRol().getNombreRol().toUpperCase();
        boolean isAdmin = rolSolicitante.equals("ADMIN") || rolSolicitante.equals("ADMINISTRADOR") || rolSolicitante.equals("SUPERADMIN");

        // Bloqueamos el intento si alguien quiere meterse a la reserva de otro cliente usando su propio JWT
        if (!isAdmin && !reserva.getUsuario().getId().equals(usuarioSolicitante.getId())) {
            throw new IllegalArgumentException("Violación de Seguridad: El token JWT no corresponde al dueño de la reserva.");
        }
                
        // Ojo, si la reserva ya pasó, no tiene sentido agregarle sillas o proyectores
        if (LocalDateTime.now().isAfter(reserva.getFechaFinReserva())) {
            throw new IllegalArgumentException("No se pueden agregar servicios a una reserva que ya ha finalizado.");
        }

        BigDecimal totalAGregar = BigDecimal.ZERO;
        List<ReservaServicio> serviciosAgregados = new java.util.ArrayList<>();

        for (Map<String, Object> req : serviciosReq) {
            Integer servicioId = ((Number) req.get("servicioId")).intValue();
            Integer cantidad = ((Number) req.get("cantidad")).intValue();
            LocalDate fechaUso = LocalDate.parse((String) req.get("fechaUso"));

            ServicioAdicional servicio = servicioAdicionalRepository.findById(servicioId)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + servicioId));

            // Asegurarnos de que pidan el servicio para los días que dura la reserva
            if (fechaUso.isBefore(reserva.getFechaInicioReserva().toLocalDate()) || 
                fechaUso.isAfter(reserva.getFechaFinReserva().toLocalDate())) {
                throw new IllegalArgumentException("La fecha de uso debe estar dentro de los días de la reserva.");
            }

            // Que no intenten pedir un servicio para ayer 😂
            if (fechaUso.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de uso no puede ser en el pasado.");
            }

            BigDecimal subtotalServicio = servicio.getPrecioBase().multiply(BigDecimal.valueOf(cantidad));
            totalAGregar = totalAGregar.add(subtotalServicio);

            // Mapeamos y guardamos el servicio
            ReservaServicio rs = new ReservaServicio();
            rs.setReserva(reserva);
            rs.setServicioAdicional(servicio);
            rs.setCantidad(cantidad);
            rs.setSubtotal(subtotalServicio);
            rs.setFechaUso(fechaUso);
            reservaServicioRepository.save(rs);
            serviciosAgregados.add(rs);
        }

        // Le sumamos el costo extra al monto total de la reserva
        if ("PENDIENTE".equalsIgnoreCase(reserva.getEstadoReserva().getNombreEstado()) || "CONFIRMADA".equalsIgnoreCase(reserva.getEstadoReserva().getNombreEstado())) {
            reserva.setMontoTotal(reserva.getMontoTotal().add(totalAGregar));
            reservaRepository.save(reserva);
        }

        return serviciosAgregados;
    }

    @Transactional
    @Auditable(accion = "CANCELAR_RESERVA", entidad = "Reservas")
    public void cancelarReserva(Integer reservaId, Usuario usuarioSolicitante) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        String rolSolicitante = usuarioSolicitante.getRol().getNombreRol().toUpperCase();
        boolean isAdmin = rolSolicitante.equals("ADMIN") || rolSolicitante.equals("ADMINISTRADOR") || rolSolicitante.equals("SUPERADMIN");

        // Por seguridad, confirmamos que el que cancela es dueño de la reserva o es admin
        if (!isAdmin && !reserva.getUsuario().getId().equals(usuarioSolicitante.getId())) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta reserva.");
        }

        String estadoActual = reserva.getEstadoReserva().getNombreEstado();
        if ("CANCELADA".equalsIgnoreCase(estadoActual) || "FINALIZADA".equalsIgnoreCase(estadoActual)) {
            throw new IllegalArgumentException("La reserva ya está " + estadoActual.toLowerCase() + ".");
        }

        // Regla estricta: No devolvemos plata ni dejamos cancelar si falta menos de un día (24h)
        // Ojo: los administradores sí pueden saltarse esta regla para casos excepcionales
        if (!isAdmin) {
            LocalDateTime limiteCancelacion = reserva.getFechaInicioReserva().minusHours(24);
            if (LocalDateTime.now().isAfter(limiteCancelacion)) {
                throw new IllegalArgumentException("No se puede cancelar la reserva con menos de 24 horas de anticipación.");
            }
        }

        EstadoReserva estadoCancelada = estadoReservaRepository.findByNombreEstado("CANCELADA")
                .orElseThrow(() -> new IllegalStateException("Estado CANCELADA no configurado en la base de datos."));

        reserva.setEstadoReserva(estadoCancelada);
        reservaRepository.save(reserva);

        // Notificamos al usuario por correo para evitar reclamos
        String subject = "Cancelación de Reserva - SpaceWork (" + reserva.getCodigoReserva() + ")";
        String body = String.format(
                "Hola %s,\n\nTu reserva para el espacio '%s' (Código: %s) ha sido CANCELADA exitosamente.\n\n" +
                "Si realizaste un pago, el proceso de reembolso (de aplicar según nuestras políticas) se iniciará en breve.\n\n" +
                "Gracias por utilizar SpaceWork.\n",
                reserva.getUsuario().getNombre(),
                reserva.getEspacio().getNombreEspacio(),
                reserva.getCodigoReserva()
        );
        emailService.sendEmail(reserva.getUsuario().getCorreoElectronico(), subject, body);
    }
}
