package com.spacework.reservations.controller;

import com.spacework.reservations.model.ServicioAdicional;
import com.spacework.reservations.service.ServicioAdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios-adicionales")
@CrossOrigin(origins = "*")
public class ServicioAdicionalController {

    @Autowired
    private ServicioAdicionalService servicioAdicionalService;

    @GetMapping
    public List<ServicioAdicional> getAll() {
        return servicioAdicionalService.getAllServicios();
    }

    @GetMapping("/activos")
    public List<ServicioAdicional> getActivos() {
        return servicioAdicionalService.getActiveServicios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioAdicional> getById(@PathVariable Integer id) {
        ServicioAdicional servicio = servicioAdicionalService.getServicioById(id);
        if (servicio != null) {
            return ResponseEntity.ok(servicio);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ServicioAdicional create(@RequestBody ServicioAdicional servicio) {
        return servicioAdicionalService.createServicio(servicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioAdicional> update(@PathVariable Integer id, @RequestBody ServicioAdicional servicio) {
        ServicioAdicional updated = servicioAdicionalService.updateServicio(id, servicio);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        servicioAdicionalService.deleteServicio(id);
        return ResponseEntity.noContent().build();
    }
}
