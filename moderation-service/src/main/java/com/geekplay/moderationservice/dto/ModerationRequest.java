package com.geekplay.moderationservice.dto;

import lombok.Data;

@Data
public class ModerationRequest {
    
    // Quién es el objetivo
    private Long userId; 
    
    // Qué contenido se elimina (Post o Comentario)
    private String contentId; 
    
    // Tipo de contenido: "POST" o "COMMENT"
    private String type; 

    // Detalles del castigo
    private String reason;
    private Integer durationMinutes; // 0 para permanente, >0 para temporal
}