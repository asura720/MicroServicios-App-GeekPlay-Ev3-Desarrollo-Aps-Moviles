package com.geekplay.moderationservice.controller;

import com.geekplay.moderationservice.dto.BanNotificationResponse;
import com.geekplay.moderationservice.dto.ModerationRequest;
import com.geekplay.moderationservice.service.ModerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderation")
@Tag(name = "Moderación", description = "API para la orquestación de castigos, eliminaciones y notificaciones de baneo")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    // GET: Devuelve DTOs con la fecha formateada
    @GetMapping("/notifications/user/{userId}")
    @Operation(summary = "Ver notificaciones de usuario", description = "Obtiene el historial de sanciones y notificaciones de moderación de un usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones recuperada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno al consultar la base de datos")
    })
    public List<BanNotificationResponse> getNotifications(@PathVariable Long userId) {
        return moderationService.getNotificationsByUserId(userId).stream()
            .map(BanNotificationResponse::new) // ✅ Convierte Entidad -> DTO aquí
            .toList();
    }
    
    // POST: Recibe el DTO de petición
    @PostMapping("/action")
    @Operation(summary = "Ejecutar acción de moderación", description = "Punto de entrada para banear usuarios y eliminar contenido. Coordina llamadas a los otros microservicios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Acción aceptada y en procesamiento (Asíncrono)"),
        @ApiResponse(responseCode = "400", description = "Datos de moderación inválidos"),
        @ApiResponse(responseCode = "500", description = "Error en la orquestación de servicios")
    })
    public ResponseEntity<Void> performAction(@RequestBody ModerationRequest request) {
        
        moderationService.executeModeration(
            request.getUserId(),
            request.getContentId(),
            request.getReason(),
            request.getDurationMinutes(),
            request.getType()
        );
        
        return new ResponseEntity<>(HttpStatus.ACCEPTED); 
    }

    // ✅ NUEVO: Endpoint DELETE
    @DeleteMapping("/notifications/{id}")
    @Operation(summary = "Eliminar notificación", description = "Borra una notificación de baneo del historial (ej. si fue leída o expiró).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificación eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "La notificación no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno al eliminar")
    })
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        moderationService.deleteNotification(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}