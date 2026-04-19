package com.sistema.licencias.controllers;

import com.sistema.licencias.entities.Rol;
import com.sistema.licencias.entities.Usuario;
import com.sistema.licencias.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");


        Optional<Usuario> usuarioOpt = usuarioRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            String token = UUID.randomUUID().toString() + "-" + usuario.getId();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuario.getNombreCompleto());
            response.put("rol", usuario.getRol().getNombre());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        if (usuario.getRol() == null || usuario.getRol().getId() == null) {
            Rol rolAdmin = new Rol();
            rolAdmin.setId(1L);
            usuario.setRol(rolAdmin);
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

}