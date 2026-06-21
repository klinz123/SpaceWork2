package com.spacework.crm.controller;

import com.spacework.crm.model.Usuario;
import com.spacework.crm.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.modelmapper.ModelMapper;
import com.spacework.crm.dto.response.UsuarioResponseDTO;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        List<UsuarioResponseDTO> dtos = usuarioService.obtenerTodos().stream()
            .map(u -> modelMapper.map(u, UsuarioResponseDTO.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Integer id, @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario usuario = usuarioService.actualizarPerfil(id, usuarioActualizado);
            return ResponseEntity.ok(modelMapper.map(usuario, UsuarioResponseDTO.class));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@PathVariable Integer id, @RequestBody java.util.Map<String, String> request) {
        try {
            String contrasenaActual = request.get("contrasenaActual");
            String nuevaContrasena = request.get("nuevaContrasena");
            usuarioService.cambiarContrasena(id, contrasenaActual, nuevaContrasena);
            return ResponseEntity.ok().body("{\"mensaje\": \"Contraseña actualizada correctamente\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> actualizarPorAdmin(@PathVariable Integer id, @RequestParam(required = false) String nombreRol, @RequestBody Usuario datosAdmin) {
        try {
            Usuario usuario = usuarioService.actualizarPorAdmin(id, datosAdmin, nombreRol);
            return ResponseEntity.ok(modelMapper.map(usuario, UsuarioResponseDTO.class));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.cambiarEstado(id, false);
            return ResponseEntity.ok().body("{\"mensaje\": \"Usuario desactivado correctamente\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/admin/{id}/desbloquear")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<?> desbloquearCuenta(@PathVariable Integer id) {
        try {
            usuarioService.desbloquearCuenta(id);
            return ResponseEntity.ok().body("{\"mensaje\": \"Cuenta desbloqueada correctamente\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
