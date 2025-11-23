package com.geekplay.interactionservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "likes")
@IdClass(LikeId.class)
@Data
@NoArgsConstructor
public class Like {

    @Id
    private Long postId;

    @Id
    private String userEmail;

    private Long timestamp = System.currentTimeMillis();


}
