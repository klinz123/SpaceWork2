package com.spacework.reservations.controller;

import com.spacework.reservations.model.Caracteristica;
import com.spacework.reservations.repository.CaracteristicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/caracteristicas")
@CrossOrigin(origins = "*")
public class CaracteristicaController {

    @Autowired
    private CaracteristicaRepository caracteristicaRepository;

    @GetMapping
    public List<Caracteristica> getAll() {
        return caracteristicaRepository.findByEstadoTrue();
    }

    @PostMapping
    public Caracteristica create(@RequestBody Caracteristica caracteristica) {
        caracteristica.setEstado(true);
        return caracteristicaRepository.save(caracteristica);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Caracteristica> update(@PathVariable Integer id, @RequestBody Caracteristica caracteristicaData) {
        Optional<Caracteristica> opt = caracteristicaRepository.findById(id);
        if (opt.isPresent()) {
            Caracteristica caracteristica = opt.get();
            caracteristica.setNombreCaracteristica(caracteristicaData.getNombreCaracteristica());
            caracteristica.setDescripcion(caracteristicaData.getDescripcion());
            caracteristica.setTipo(caracteristicaData.getTipo());
            caracteristica.setEstado(caracteristicaData.isEstado());
            return ResponseEntity.ok(caracteristicaRepository.save(caracteristica));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<Caracteristica> opt = caracteristicaRepository.findById(id);
        if (opt.isPresent()) {
            Caracteristica caracteristica = opt.get();
            caracteristica.setEstado(false);
            caracteristicaRepository.save(caracteristica);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
