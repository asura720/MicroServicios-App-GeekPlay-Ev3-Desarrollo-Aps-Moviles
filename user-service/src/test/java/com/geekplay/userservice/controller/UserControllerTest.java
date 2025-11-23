package com.geekplay.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekplay.userservice.dto.ChangePasswordRequest;
import com.geekplay.userservice.dto.UpdateProfileRequest;
import com.geekplay.userservice.model.User;
import com.geekplay.userservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setPhone("123456789");
        testUser.setProfileImagePath("/images/profile.jpg");
    }

    @Test
    void getUserById_Success() throws Exception {
        when(authService.findUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(authService.findUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        when(authService.findUserByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getUserByEmail_NotFound() throws Exception {
        when(authService.findUserByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email/notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");
        request.setPhone("987654321");
        request.setProfileImagePath("/images/newprofile.jpg");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("test@example.com");
        updatedUser.setPhone("987654321");
        updatedUser.setProfileImagePath("/images/newprofile.jpg");

        when(authService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("987654321"));
    }

    @Test
    void updateProfile_UserNotFound() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");

        when(authService.updateProfile(eq(999L), any(UpdateProfileRequest.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        doNothing().when(authService).changePassword(eq(1L), anyString(), anyString());

        mockMvc.perform(put("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada correctamente"));
    }

    @Test
    void changePassword_IncorrectCurrentPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");

        doThrow(new IllegalArgumentException("La contraseña actual es incorrecta"))
                .when(authService).changePassword(eq(1L), anyString(), anyString());

        mockMvc.perform(put("/api/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La contraseña actual es incorrecta"));
    }

    @Test
    void changePassword_UserNotFound() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        doThrow(new RuntimeException("Usuario no encontrado"))
                .when(authService).changePassword(eq(999L), anyString(), anyString());

        mockMvc.perform(put("/api/users/999/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void updateBannedUntil_Success() throws Exception {
        Long bannedUntil = System.currentTimeMillis() + 3600000;

        doNothing().when(authService).updateBanStatus(eq(1L), eq(bannedUntil));

        mockMvc.perform(put("/api/users/1/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bannedUntil.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBannedUntil_Error() throws Exception {
        Long bannedUntil = System.currentTimeMillis() + 3600000;

        doThrow(new RuntimeException("Database error"))
                .when(authService).updateBanStatus(eq(1L), eq(bannedUntil));

        mockMvc.perform(put("/api/users/1/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bannedUntil.toString()))
                .andExpect(status().isInternalServerError());
    }
}
