package com.geekplay.interactionservice.dto;

import lombok.Data;

@Data
public class UserClientResponse {
    private Long id;
    private String name;
    private String profileImagePath;
}