package com.geekplay.userservice.controller;

import com.geekplay.userservice.dto.LoginRequest;
import com.geekplay.userservice.dto.UserResponse;
import com.geekplay.userservice.model.User;
import com.geekplay.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses; // ⬅️ Importante
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints públicos para registro e inicio de sesión")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Conflicto: El email ya está registrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> register(@RequestBody User newUser) {
        try {
            User registeredUser = authService.register(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(registeredUser));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar Sesión", description = "Valida credenciales y retorna datos del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "No autorizado: Credenciales inválidas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> authenticatedUser = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (authenticatedUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(authenticatedUser.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}