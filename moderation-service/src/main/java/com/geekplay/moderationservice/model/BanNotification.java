package com.geekplay.moderationservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ban_notifications")
@Data
@NoArgsConstructor
public class BanNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId; // ID del usuario baneado (referencia al User Service)
    private String reason; // Motivo del baneo
    private String duration; // Texto: "5 minutos", "Permanente"
    private String appealGuide; // Texto de ayuda
    
    private Long timestamp = System.currentTimeMillis(); // Fecha automática
    private Boolean isRead = false; // Estado de lectura
}