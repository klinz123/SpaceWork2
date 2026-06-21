package com.spacework.reservations.controller;

import com.spacework.reservations.model.Ubicacion;
import com.spacework.reservations.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ubicaciones")
@CrossOrigin(origins = "*")
public class UbicacionController {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @GetMapping
    public List<Ubicacion> getAll() {
        return ubicacionRepository.findByEstadoTrue();
    }

    @PostMapping
    public Ubicacion create(@RequestBody Ubicacion ubicacion) {
        ubicacion.setEstado(true);
        return ubicacionRepository.save(ubicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> update(@PathVariable Integer id, @RequestBody Ubicacion ubicacionData) {
        Optional<Ubicacion> opt = ubicacionRepository.findById(id);
        if (opt.isPresent()) {
            Ubicacion ubicacion = opt.get();
            ubicacion.setNombreUbicacion(ubicacionData.getNombreUbicacion());
            ubicacion.setDireccion(ubicacionData.getDireccion());
            ubicacion.setCiudad(ubicacionData.getCiudad());
            ubicacion.setPais(ubicacionData.getPais());
            ubicacion.setLatitud(ubicacionData.getLatitud());
            ubicacion.setLongitud(ubicacionData.getLongitud());
            ubicacion.setUrlGoogleMaps(ubicacionData.getUrlGoogleMaps());
            ubicacion.setEstado(ubicacionData.isEstado());
            return ResponseEntity.ok(ubicacionRepository.save(ubicacion));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<Ubicacion> opt = ubicacionRepository.findById(id);
        if (opt.isPresent()) {
            Ubicacion ubicacion = opt.get();
            ubicacion.setEstado(false);
            ubicacionRepository.save(ubicacion);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
