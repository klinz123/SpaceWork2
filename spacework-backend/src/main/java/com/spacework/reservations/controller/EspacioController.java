package com.spacework.reservations.controller;

import com.spacework.reservations.model.Espacio;
import com.spacework.reservations.model.Precio;
import com.spacework.reservations.repository.EspacioRepository;
import com.spacework.reservations.repository.TipoEspacioRepository;
import com.spacework.reservations.repository.UbicacionRepository;
import com.spacework.reservations.repository.CaracteristicaRepository;
import com.spacework.reservations.repository.PrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import com.spacework.reservations.dto.request.EspacioRequestDTO;
import com.spacework.reservations.dto.response.EspacioResponseDTO;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/espacios")

public class EspacioController {

    private final EspacioRepository espacioRepository;
    private final TipoEspacioRepository tipoEspacioRepository;
    private final UbicacionRepository ubicacionRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final PrecioRepository precioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EspacioController(EspacioRepository espacioRepository,
                             TipoEspacioRepository tipoEspacioRepository,
                             UbicacionRepository ubicacionRepository,
                             CaracteristicaRepository caracteristicaRepository,
                             PrecioRepository precioRepository, ModelMapper modelMapper) {
        this.espacioRepository = espacioRepository;
        this.tipoEspacioRepository = tipoEspacioRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.precioRepository = precioRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<EspacioResponseDTO>> listar() {
        List<Espacio> espacios = espacioRepository.findByEstadoTrue();
        for (Espacio e : espacios) {
            precioRepository.findFirstByEspacioIdAndEstadoTrue(e.getId())
                    .ifPresent(p -> {
                        e.setPrecio(p.getMonto());
                        e.setDescuento(p.getDescuento());
                    });
        }
        List<EspacioResponseDTO> dtos = espacios.stream()
            .map(e -> modelMapper.map(e, EspacioResponseDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<EspacioResponseDTO>> filtrar(@RequestParam Integer tipoEspacioId) {
        List<Espacio> espacios = espacioRepository.findByTipoEspacioIdAndEstadoTrue(tipoEspacioId);
        for (Espacio e : espacios) {
            precioRepository.findFirstByEspacioIdAndEstadoTrue(e.getId())
                    .ifPresent(p -> {
                        e.setPrecio(p.getMonto());
                        e.setDescuento(p.getDescuento());
                    });
        }
        List<EspacioResponseDTO> dtos = espacios.stream()
            .map(e -> modelMapper.map(e, EspacioResponseDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipos")
    public ResponseEntity<?> listarTipos() {
        return ResponseEntity.ok(tipoEspacioRepository.findByEstadoTrue());
    }

    @GetMapping("/ubicaciones")
    public ResponseEntity<?> listarUbicaciones() {
        return ResponseEntity.ok(ubicacionRepository.findByEstadoTrue());
    }

    @GetMapping("/caracteristicas")
    public ResponseEntity<?> listarCaracteristicas() {
        return ResponseEntity.ok(caracteristicaRepository.findByEstadoTrue());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> crear(@RequestBody EspacioRequestDTO requestDTO) {
        try {
            Espacio espacio = modelMapper.map(requestDTO, Espacio.class);
            // Autogeneración inteligente del Código de Espacio
            String prefijoTipo = generarPrefijoTipo(espacio.getTipoEspacio() != null && espacio.getTipoEspacio().getId() != null ? 
                tipoEspacioRepository.findById(espacio.getTipoEspacio().getId()).map(t -> t.getNombreTipo()).orElse("") : "");
            String prefijoSede = generarPrefijoSede(espacio.getUbicacion() != null && espacio.getUbicacion().getId() != null ? 
                ubicacionRepository.findById(espacio.getUbicacion().getId()).map(u -> u.getNombreUbicacion()).orElse("") : "");
            
            String prefijoBase = prefijoTipo + "-" + prefijoSede;
            
            // Buscar correlativo máximo
            int maxCorrelativo = 0;
            List<Espacio> todos = espacioRepository.findAll();
            for (Espacio e : todos) {
                if (e.getCodigoEspacio() != null && e.getCodigoEspacio().startsWith(prefijoBase + "-")) {
                    try {
                        String[] parts = e.getCodigoEspacio().split("-");
                        int num = Integer.parseInt(parts[parts.length - 1]);
                        if (num > maxCorrelativo) {
                            maxCorrelativo = num;
                        }
                    } catch (Exception ex) {
                        // ignorar si no es un número
                    }
                }
            }
            
            String nuevoCodigo = String.format("%s-%03d", prefijoBase, maxCorrelativo + 1);
            espacio.setCodigoEspacio(nuevoCodigo);
            if (espacio.getHoraApertura() == null) {
                espacio.setHoraApertura(java.time.LocalTime.of(8, 0));
            }
            if (espacio.getHoraCierre() == null) {
                espacio.setHoraCierre(java.time.LocalTime.of(20, 0));
            }
            espacio.setEstado(true);
            
            if (espacio.getCapacidadEquipos() == null) {
                espacio.setCapacidadEquipos(espacio.getCapacidad());
            }
            if (espacio.getPrecioPersonaExtra() == null) {
                espacio.setPrecioPersonaExtra(0.0);
            }
            
            if (espacio.getCaracteristicas() != null && !espacio.getCaracteristicas().isEmpty()) {
                Set<com.spacework.reservations.model.Caracteristica> dbCarac = new HashSet<>();
                for (com.spacework.reservations.model.Caracteristica c : espacio.getCaracteristicas()) {
                    caracteristicaRepository.findById(c.getId()).ifPresent(dbCarac::add);
                }
                espacio.setCaracteristicas(dbCarac);
            }

            if (espacio.getFotos() != null && !espacio.getFotos().isEmpty()) {
                boolean hasPrincipal = false;
                for (com.spacework.reservations.model.FotoEspacio f : espacio.getFotos()) {
                    f.setEspacio(espacio);
                    if (f.isEsPrincipal()) {
                        hasPrincipal = true;
                        espacio.setFotoUrl(f.getUrlFoto()); // Sincronizar campo legado
                    }
                }
                // Si ninguna es principal, forzar la primera
                if (!hasPrincipal && !espacio.getFotos().isEmpty()) {
                    espacio.getFotos().get(0).setEsPrincipal(true);
                    espacio.setFotoUrl(espacio.getFotos().get(0).getUrlFoto());
                }
            }

            Espacio nuevo = espacioRepository.save(espacio);

            // guardamos precio en la tabla Precios
            if (espacio.getPrecio() != null) {
                Precio precioEntity = new Precio();
                precioEntity.setEspacio(nuevo);
                precioEntity.setMonto(espacio.getPrecio());
                if (espacio.getDescuento() != null) {
                    precioEntity.setDescuento(espacio.getDescuento());
                }
                precioEntity.setTipoTarifa("DIA");
                precioEntity.setMoneda("PEN");
                precioEntity.setEstado(true);
                precioRepository.save(precioEntity);
                nuevo.setPrecio(espacio.getPrecio());
                nuevo.setDescuento(espacio.getDescuento());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(nuevo, EspacioResponseDTO.class));
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody EspacioRequestDTO requestDTO) {
        try {
            Espacio espacioData = modelMapper.map(requestDTO, Espacio.class);
            Espacio espacio = espacioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado."));

            espacio.setCodigoEspacio(espacioData.getCodigoEspacio());
            espacio.setNombreEspacio(espacioData.getNombreEspacio());
            espacio.setCapacidad(espacioData.getCapacidad());
            espacio.setDescripcion(espacioData.getDescripcion());
            espacio.setEstadoEspacio(espacioData.getEstadoEspacio());
            espacio.setFotoUrl(espacioData.getFotoUrl());
            espacio.setMetrosCuadrados(espacioData.getMetrosCuadrados());
            espacio.setCapacidadEquipos(espacioData.getCapacidadEquipos());
            espacio.setPrecioPersonaExtra(espacioData.getPrecioPersonaExtra());
            
            if (espacioData.getHoraApertura() != null) espacio.setHoraApertura(espacioData.getHoraApertura());
            if (espacioData.getHoraCierre() != null) espacio.setHoraCierre(espacioData.getHoraCierre());
            
            if (espacioData.getTipoEspacio() != null) {
                tipoEspacioRepository.findById(espacioData.getTipoEspacio().getId()).ifPresent(espacio::setTipoEspacio);
            }
            if (espacioData.getUbicacion() != null) {
                ubicacionRepository.findById(espacioData.getUbicacion().getId()).ifPresent(espacio::setUbicacion);
            }
            
            if (espacioData.getCaracteristicas() != null) {
                Set<com.spacework.reservations.model.Caracteristica> dbCarac = new HashSet<>();
                for (com.spacework.reservations.model.Caracteristica c : espacioData.getCaracteristicas()) {
                    caracteristicaRepository.findById(c.getId()).ifPresent(dbCarac::add);
                }
                espacio.setCaracteristicas(dbCarac);
            }

            // Gestionar fotos: reemplazar lista actual con la nueva
            if (espacioData.getFotos() != null) {
                espacio.getFotos().clear();
                boolean hasPrincipal = false;
                for (com.spacework.reservations.model.FotoEspacio f : espacioData.getFotos()) {
                    f.setId(null); // Evitar error de entidad 'detached'
                    f.setEspacio(espacio);
                    espacio.getFotos().add(f);
                    if (f.isEsPrincipal()) {
                        hasPrincipal = true;
                        espacio.setFotoUrl(f.getUrlFoto());
                    }
                }
                if (!hasPrincipal && !espacio.getFotos().isEmpty()) {
                    espacio.getFotos().get(0).setEsPrincipal(true);
                    espacio.setFotoUrl(espacio.getFotos().get(0).getUrlFoto());
                } else if (espacio.getFotos().isEmpty()) {
                    espacio.setFotoUrl(null);
                }
            }

            Espacio actualizado = espacioRepository.save(espacio);

            // guardamos/Actualizar precio en la tabla Precios
            if (espacioData.getPrecio() != null) {
                Precio precioEntity = precioRepository.findFirstByEspacioIdAndEstadoTrue(actualizado.getId())
                        .orElse(null);
                if (precioEntity != null) {
                    precioEntity.setMonto(espacioData.getPrecio());
                    if (espacioData.getDescuento() != null) {
                        precioEntity.setDescuento(espacioData.getDescuento());
                    } else {
                        precioEntity.setDescuento(java.math.BigDecimal.ZERO);
                    }
                } else {
                    precioEntity = new Precio();
                    precioEntity.setEspacio(actualizado);
                    precioEntity.setMonto(espacioData.getPrecio());
                    if (espacioData.getDescuento() != null) {
                        precioEntity.setDescuento(espacioData.getDescuento());
                    }
                    precioEntity.setTipoTarifa("DIA");
                    precioEntity.setMoneda("PEN");
                    precioEntity.setEstado(true);
                }
                precioRepository.save(precioEntity);
                actualizado.setPrecio(espacioData.getPrecio());
                actualizado.setDescuento(espacioData.getDescuento());
            }

            return ResponseEntity.ok(modelMapper.map(actualizado, EspacioResponseDTO.class));
        } catch (Exception e) {
            e.printStackTrace();
            
            // Capture full stack trace for diagnostics
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));

            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            Espacio espacio = espacioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado."));
            espacio.setEstado(false);
            espacioRepository.save(espacio);
            Map<String, String> res = new HashMap<>();
            res.put("mensaje", "Espacio eliminado correctamente.");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    // Helper methods para la autogeneración de códigos
    private String generarPrefijoTipo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "XX";
        String n = nombre.toUpperCase().trim();
        if (n.contains("COWORKING")) return "CW";
        if (n.contains("OFICINA")) return "OF";
        if (n.contains("SALA DE REUNIONES") || n.contains("SALA")) return "SR";
        
        // Fallback
        String[] parts = n.split("\\s+");
        if (parts.length == 1) {
            return parts[0].length() >= 3 ? parts[0].substring(0, 3) : parts[0];
        } else {
            StringBuilder sb = new StringBuilder();
            for(String p : parts) {
                if(!p.equals("DE") && !p.equals("LA") && !p.equals("EL")) {
                    sb.append(p.charAt(0));
                }
            }
            return sb.length() > 3 ? sb.substring(0,3) : sb.toString();
        }
    }

    private String generarPrefijoSede(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "XXX";
        String n = nombre.toUpperCase().trim().replace("SEDE", "").trim();
        
        if (n.contains("SAN ISIDRO")) return "ISI";
        if (n.contains("MIRAFLORES")) return "MIR";
        
        // Fallback
        String[] parts = n.split("\\s+");
        if (parts.length == 1) {
            return parts[0].length() >= 3 ? parts[0].substring(0, 3) : parts[0];
        } else {
            return parts[0].length() >= 3 ? parts[0].substring(0, 3) : parts[0];
        }
    }
}

