package com.geekplay.userservice.controller;

import com.geekplay.userservice.dto.ChangePasswordRequest;
import com.geekplay.userservice.dto.UpdateProfileRequest;
import com.geekplay.userservice.dto.UserResponse;
import com.geekplay.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Gestión de Usuarios", description = "Operaciones de perfil y administración")
public class UserController {

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener perfil", description = "Busca un usuario por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return authService.findUserById(id)
            .map(UserResponse::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener perfil por email", description = "Busca un usuario por su correo electrónico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return authService.findUserByEmail(email)
            .map(UserResponse::new)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar perfil", description = "Modifica nombre, teléfono o foto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        try {
            return ResponseEntity.ok(new UserResponse(authService.updateProfile(id, request)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Cambiar contraseña", description = "Actualiza la contraseña validando la actual.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
        @ApiResponse(responseCode = "400", description = "Petición incorrecta: La contraseña actual no coincide"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PutMapping("/{id}/ban")
    @Operation(summary = "Banear usuario", description = "Endpoint interno para actualizar estado de baneo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estado actualizado (Sin contenido)"),
        @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<Void> updateBannedUntil(@PathVariable Long id, @RequestBody Long bannedUntil) {
        try {
            authService.updateBanStatus(id, bannedUntil);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}