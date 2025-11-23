package com.geekplay.contentservice.dto;

import com.geekplay.contentservice.model.Post;
import lombok.Data;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class PostResponse {
    
    private Long id; 
    private String title;
    private String summary;
    private String content;
    private String category; 
    private Long authorId; 
    private String imageUrl;
    private String publishedAt; 

    // 🆕 CAMPOS NUEVOS
    private String authorName;
    private String authorProfileImageUrl;
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    // ✅ Constructor actualizado para recibir datos enriquecidos
    public PostResponse(Post post, String authorName, String authorProfileImageUrl) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.summary = post.getSummary();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.authorId = post.getAuthorId();
        this.imageUrl = post.getImageUrl();
        
        // Asignar nuevos datos
        this.authorName = authorName;
        this.authorProfileImageUrl = authorProfileImageUrl;

        if (post.getPublishedAt() != null) {
            this.publishedAt = FORMATTER.format(Instant.ofEpochMilli(post.getPublishedAt()));
        }
    }
}