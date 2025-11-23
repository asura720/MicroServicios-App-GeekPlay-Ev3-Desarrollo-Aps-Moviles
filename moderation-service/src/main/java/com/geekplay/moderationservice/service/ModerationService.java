package com.geekplay.moderationservice.service;

import com.geekplay.moderationservice.model.BanNotification;
import com.geekplay.moderationservice.repository.BanNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.List;

@Service
public class ModerationService {

    @Autowired
    private BanNotificationRepository notificationRepository;

    @Autowired
    private WebClient webClient; // ⬅️ Nuestro cliente configurado

    // URLs de los otros servicios (desde application.properties)
    @Value("${user.service.base-url}")
    private String userServiceUrl;
    
    @Value("${content.service.base-url}")
    private String contentServiceUrl;

    @Value("${interaction.service.base-url}")
    private String interactionServiceUrl;

    // --- Lectura de Notificaciones ---
    
    @Transactional(readOnly = true)
    public List<BanNotification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // --- Lógica de Moderación (Orquestación) ---

    @Transactional
    public void executeModeration(Long targetUserId, String contentId, String reason, int durationMinutes, String type) {
        
        // 1. Calcular tiempo de baneo
        Long bannedUntil = (durationMinutes > 0) ? 
                           System.currentTimeMillis() + (long) durationMinutes * 60 * 1000 : null;
        String durationText = (durationMinutes > 0) ? durationMinutes + " minutos" : "Permanente";
        
        // 2. Llamar a USER SERVICE (8083) para banear al usuario
        // PUT /api/users/{id}/ban
        try {
            webClient.put()
                .uri(userServiceUrl + "/{id}/ban", targetUserId)
                .bodyValue(bannedUntil != null ? bannedUntil : 0L) // Enviar 0L si es null para evitar error de serialización
                .retrieve()
                .toBodilessEntity()
                .block(); // Bloqueamos para asegurar que se ejecute
        } catch (Exception e) {
            System.err.println("Error al contactar User Service: " + e.getMessage());
        }
            
        // 3. Llamar al servicio correspondiente para ELIMINAR el contenido
        String notificationTitle = "Aviso de Moderación";

        try {
            if ("POST".equalsIgnoreCase(type)) {
                // DELETE Content Service (8081) -> /api/posts/{id}
                webClient.delete()
                    .uri(contentServiceUrl + "/{id}", contentId)
                    .retrieve().toBodilessEntity().block();
                notificationTitle = "Tu publicación ha sido eliminada.";

            } else if ("COMMENT".equalsIgnoreCase(type)) {
                // DELETE Interaction Service (8082) -> /api/interactions/comments/{id}
                webClient.delete()
                    .uri(interactionServiceUrl + "/comments/{id}", contentId)
                    .retrieve().toBodilessEntity().block();
                notificationTitle = "Tu comentario ha sido eliminado.";
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar contenido: " + e.getMessage());
        }

        // 4. Guardar la Notificación localmente
        BanNotification notification = new BanNotification();
        notification.setUserId(targetUserId);
        notification.setReason(notificationTitle + " Motivo: " + reason);
        notification.setDuration(durationText);
        notification.setAppealGuide("Contacta a soporte@geekplay.cl para apelar.");
        
        notificationRepository.save(notification);
    }

    // ✅ NUEVO: Eliminar notificación por ID
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}