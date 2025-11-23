package com.geekplay.interactionservice.dto;

import com.geekplay.interactionservice.model.Comment;
import lombok.Data;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class CommentResponse {
    
    private String id;
    private Long postId;
    private Long authorId;
    
    // ⬅️ Datos enriquecidos del User Service
    private String authorName; 
    private String authorProfileImageUrl;
    
    private String content;
    private String timestamp; // Fecha legible

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

    public CommentResponse(Comment comment, String authorName, String authorProfileImageUrl) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.authorId = comment.getAuthorId();
        this.content = comment.getContent();
        
        // Asignación de datos enriquecidos:
        this.authorName = authorName;
        this.authorProfileImageUrl = authorProfileImageUrl;
        
        // Formateo de fecha:
        if (comment.getTimestamp() != null) {
            this.timestamp = FORMATTER.format(
                Instant.ofEpochMilli(comment.getTimestamp())
            );
        } else {
            this.timestamp = null;
        }
    }
}