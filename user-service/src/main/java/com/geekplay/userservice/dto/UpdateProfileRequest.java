package com.geekplay.userservice.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phone;
    private String profileImagePath; // Se recibe la URL o ruta de la imagen
}