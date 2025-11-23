package com.geekplay.contentservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "posts")
@Data 
@NoArgsConstructor
@AllArgsConstructor 
public class Post {

    // 🚨 CAMBIO 1: ID Secuencial y Autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // El tipo de ID ahora es Long

    private String title;
    private String summary;
    
    @Column(columnDefinition = "TEXT") 
    private String content;
    
    private String category; 
    
    private Long authorId; 
    
    private Long publishedAt; // Se mantiene Long (Timestamp del PC)
    private String imageUrl;
}