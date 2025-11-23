package com.geekplay.interactionservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    private String id;
    
    private Long postId;

    private Long authorId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long timestamp;

}
