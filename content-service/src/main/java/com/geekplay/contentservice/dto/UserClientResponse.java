package com.geekplay.contentservice.dto;

import lombok.Data;

@Data
public class UserClientResponse {
    private Long id;
    private String name;
    private String profileImagePath;
}