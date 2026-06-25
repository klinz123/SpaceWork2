package com.spacework.core.security;

import com.spacework.crm.model.Usuario;
import com.spacework.crm.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import com.spacework.crm.dto.request.UsuarioLoginRequestDTO;
import com.spacework.crm.dto.request.UsuarioRegistroRequestDTO;
import com.spacework.crm.dto.response.UsuarioResponseDTO;
import com.spacework.auditoria.aop.Auditable;


@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final UsuarioService usuarioService;
    private final LoginAttemptService loginAttemptService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(UsuarioService usuarioService, LoginAttemptService loginAttemptService, JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.loginAttemptService = loginAttemptService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/registro")
    @Auditable(accion = "REGISTRO_USUARIO", entidad = "Usuarios")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioRegistroRequestDTO requestDTO, 
                                       @RequestParam(defaultValue = "DNI") String tipoDoc) {
        try {
            Usuario usuario = modelMapper.map(requestDTO, Usuario.class);
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario, "CLIENTE", tipoDoc);
            UsuarioResponseDTO responseDTO = modelMapper.map(nuevoUsuario, UsuarioResponseDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    @Auditable(accion = "LOGIN_USUARIO", entidad = "Usuarios")
    public ResponseEntity<?> login(@Valid @RequestBody UsuarioLoginRequestDTO requestDTO, HttpServletResponse httpResponse) {
        String correo = requestDTO.getCorreoElectronico();
        String contrasena = requestDTO.getContrasena();

        if (correo == null || contrasena == null) {
            return ResponseEntity.badRequest().body("Faltan credenciales.");
        }

        // guava Cache Rate Limiting / Security block check
        if (loginAttemptService.isBlocked(correo)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cuenta bloqueada por seguridad. Contacte al administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.login(correo, contrasena);
            if (usuarioOpt.isPresent()) {
                loginAttemptService.loginSucceeded(correo);
                usuarioService.resetearIntentos(correo);
                Usuario usuario = usuarioOpt.get();
                String token = jwtTokenProvider.generateToken(usuario.getCorreoElectronico(), usuario.getRol().getNombreRol());
                
                Cookie cookie = new Cookie("jwt", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(10 * 60 * 60); // 10 horas
                httpResponse.addCookie(cookie);
                
                Map<String, Object> response = new HashMap<>();
                response.put("accessToken", token);
                response.put("usuario", modelMapper.map(usuario, UsuarioResponseDTO.class));
                return ResponseEntity.ok(response);
            } else {
                loginAttemptService.loginFailed(correo);
                usuarioService.incrementarIntentosFallidos(correo);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciales incorrectas.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }
}
