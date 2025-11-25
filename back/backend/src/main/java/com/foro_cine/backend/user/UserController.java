package com.foro_cine.backend.user;

import com.foro_cine.backend.user.dto.CreateUserRequest;
import com.foro_cine.backend.user.dto.LoginRequest;
import com.foro_cine.backend.user.dto.LoginResponse;
import com.foro_cine.backend.user.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------
    // CREATE USER
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest request) {

        if (userRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = User.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .contrasena(request.getContraseña())
                .ubicacion(request.getUbicacion())
                .profileImageUrl(request.getProfileImageUrl())
                .role(request.getRole())
                .build();

        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        return userRepository.findById(id)
                .map(existing -> {

                    if (request.getNombre() != null)
                        existing.setNombre(request.getNombre());

                    if (request.getCorreo() != null)
                        existing.setCorreo(request.getCorreo());

                    if (request.getContraseña() != null)
                        existing.setContrasena(request.getContraseña());

                    if (request.getUbicacion() != null)
                        existing.setUbicacion(request.getUbicacion());

                    if (request.getProfileImageUrl() != null)
                        existing.setProfileImageUrl(request.getProfileImageUrl());

                    if (request.getRole() != null)
                        existing.setRole(request.getRole());

                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        return userRepository.findByCorreo(request.getCorreo())
                .filter(user -> user.getContrasena().equals(request.getContraseña()))
                .map(user -> ResponseEntity.ok(
                        new LoginResponse(
                                user.getId(),
                                user.getNombre(),
                                user.getCorreo(),
                                user.getUbicacion(),
                                user.getProfileImageUrl(),
                                user.getRole()
                        )
                ))
                .orElse(ResponseEntity.status(401).build());
    }
}
