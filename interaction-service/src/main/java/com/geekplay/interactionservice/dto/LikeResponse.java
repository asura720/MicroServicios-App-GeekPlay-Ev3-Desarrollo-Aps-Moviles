package com.geekplay.interactionservice.dto;

import lombok.Data;

@Data
public class LikeResponse {
    private Long postId;
    private String userEmail;
    private String userName; // ⬅️ Dato enriquecido
    private String userProfileImageUrl; // ⬅️ Dato enriquecido
    private Long timestamp;

    public LikeResponse(Long postId, String userEmail, String userName, String userProfileImageUrl, Long timestamp) {
        this.postId = postId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
        this.timestamp = timestamp;
    }
}