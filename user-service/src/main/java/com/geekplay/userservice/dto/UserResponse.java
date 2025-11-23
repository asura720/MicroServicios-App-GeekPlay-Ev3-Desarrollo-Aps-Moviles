package com.geekplay.userservice.dto;

import com.geekplay.userservice.model.User;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String profileImagePath;
    private boolean isAdmin;
    private Long bannedUntil;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profileImagePath = user.getProfileImagePath();
        this.isAdmin = user.isAdmin();
        this.bannedUntil = user.getBannedUntil();
    }
}