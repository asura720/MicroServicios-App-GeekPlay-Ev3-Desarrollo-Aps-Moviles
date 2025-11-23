package com.geekplay.moderationservice.dto;

import com.geekplay.moderationservice.model.BanNotification;
import lombok.Data;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class BanNotificationResponse {

    private Long id;
    private String reason;
    private String duration;
    private String appealGuide;
    private String timestamp; // ⬅️ Fecha legible (String)
    private Boolean isRead;

    // Formateador de fecha
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    public BanNotificationResponse(BanNotification notification) {
        this.id = notification.getId();
        this.reason = notification.getReason();
        this.duration = notification.getDuration();
        this.appealGuide = notification.getAppealGuide();
        this.isRead = notification.getIsRead();
        
        // Conversión de milisegundos a fecha legible
        if (notification.getTimestamp() != null) {
            this.timestamp = FORMATTER.format(Instant.ofEpochMilli(notification.getTimestamp()));
        }
    }
}