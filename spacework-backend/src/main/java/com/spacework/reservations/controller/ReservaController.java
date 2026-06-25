package com.spacework.reservations.controller;

import com.spacework.reservations.model.Espacio;
import com.spacework.reservations.model.Reserva;
import com.spacework.crm.model.Usuario;
import com.spacework.reservations.repository.EspacioRepository;
import com.spacework.reservations.repository.PrecioRepository;
import com.spacework.reservations.repository.ReservaRepository;
import com.spacework.crm.repository.UsuarioRepository;
import com.spacework.reservations.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import com.spacework.reservations.dto.request.ReservaRequestDTO;
import com.spacework.reservations.dto.response.ReservaResponseDTO;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;
    private final PrecioRepository precioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservaController(ReservaService reservaService, 
                             ReservaRepository reservaRepository,
                             UsuarioRepository usuarioRepository, 
                             EspacioRepository espacioRepository,
                             PrecioRepository precioRepository, ModelMapper modelMapper) {
        this.reservaService = reservaService;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
        this.precioRepository = precioRepository;
        this.modelMapper = modelMapper;
    }

    // Endpoint para el admin (lista todas las reservas sin filtro)
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        List<Reserva> reservas = reservaRepository.findAll();
        enriquecerConPrecio(reservas);
        List<ReservaResponseDTO> dtos = reservas.stream()
            .map(r -> modelMapper.map(r, ReservaResponseDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Endpoint usado por "Mis Reservas" en el Frontend
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(@PathVariable Integer usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
        enriquecerConPrecio(reservas);
        List<ReservaResponseDTO> dtos = reservas.stream()
            .map(r -> modelMapper.map(r, ReservaResponseDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaRequestDTO body) {
        try {
            Integer usuarioId = body.getUsuarioId();
            Integer espacioId = body.getEspacioId();
            String inicioStr = body.getFechaInicio();
            String finStr = body.getFechaFin();
            Double montoDouble = body.getMontoTotal();
            Double descuentoDouble = body.getDescuentoAplicado() != null ? body.getDescuentoAplicado() : 0.0;
            String observaciones = body.getObservaciones();
            String tipoComprobante = body.getTipoComprobante();
            String documentoIdentidad = body.getDocumentoIdentidad();
            String razonSocial = body.getRazonSocial();
            String direccion = body.getDireccion();
            
            Map<Integer, Integer> serviciosAdicionales = new HashMap<>();
            if (body.getServiciosAdicionales() != null) {
                for (Map.Entry<String, Integer> entry : body.getServiciosAdicionales().entrySet()) {
                    serviciosAdicionales.put(Integer.valueOf(entry.getKey()), entry.getValue());
                }
            }

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
            Espacio espacio = espacioRepository.findById(espacioId)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado."));

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, formatter);
            LocalDateTime fin = LocalDateTime.parse(finStr, formatter);

            if (montoDouble == null) {
                throw new IllegalArgumentException("El monto total es requerido.");
            }
            BigDecimal montoTotal = BigDecimal.valueOf(montoDouble);
            BigDecimal descuentoAplicado = BigDecimal.valueOf(descuentoDouble);

            Reserva reserva = reservaService.crearReserva(usuario, espacio, inicio, fin, montoTotal, descuentoAplicado, observaciones, serviciosAdicionales, tipoComprobante, documentoIdentidad, razonSocial, direccion);
            // Parche temporal: el DTO necesita el precio del espacio y el descuento que está en otra tabla.
            // TODO: Hacer un JOIN limpio en el repositorio en vez de lanzar otra query aquí
            precioRepository.findFirstByEspacioIdAndEstadoTrue(reserva.getEspacio().getId())
                    .ifPresent(p -> {
                        reserva.getEspacio().setPrecio(p.getMonto());
                        reserva.getEspacio().setDescuento(p.getDescuento());
                    });
            return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(reserva, ReservaResponseDTO.class));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/agregar-servicio")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> agregarServicioExtra(@PathVariable Integer id, @RequestBody List<Map<String, Object>> body, org.springframework.security.core.Authentication authentication) {
        try {
            Usuario usuarioSolicitante = (Usuario) authentication.getPrincipal();
            List<com.spacework.reservations.model.ReservaServicio> rsList = reservaService.agregarServiciosExtra(id, body, usuarioSolicitante);
            return ResponseEntity.ok(rsList);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/cancelar")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelarReserva(@PathVariable Integer id, org.springframework.security.core.Authentication authentication) {
        try {
            Usuario usuarioSolicitante = (Usuario) authentication.getPrincipal();
            reservaService.cancelarReserva(id, usuarioSolicitante);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Reserva cancelada exitosamente.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno al cancelar la reserva.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Hack para solucionar el problema del precio que no venía en la entidad Espacio.
     * Itera las reservas y pega el precio a mano.
     * TODO: Optimizar esto, puede causar un problema de N+1 queries si hay muchas reservas.
     */
    private void enriquecerConPrecio(List<Reserva> reservas) {
        List<Integer> espacioIds = reservas.stream()
                .filter(r -> r.getEspacio() != null)
                .map(r -> r.getEspacio().getId())
                .distinct()
                .collect(Collectors.toList());

        if (espacioIds.isEmpty()) return;

        List<com.spacework.reservations.model.Precio> precios = precioRepository.findByEspacioIdInAndEstadoTrue(espacioIds);
        Map<Integer, com.spacework.reservations.model.Precio> precioMap = precios.stream()
                .collect(Collectors.toMap(p -> p.getEspacio().getId(), p -> p, (p1, p2) -> p1));

        for (Reserva r : reservas) {
            if (r.getEspacio() != null) {
                com.spacework.reservations.model.Precio p = precioMap.get(r.getEspacio().getId());
                if (p != null) {
                    r.getEspacio().setPrecio(p.getMonto());
                    r.getEspacio().setDescuento(p.getDescuento());
                }
            }
        }
    }
}
