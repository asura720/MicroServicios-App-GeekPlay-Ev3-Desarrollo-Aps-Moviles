package com.geekplay.userservice.service;

import com.geekplay.userservice.dto.UpdateProfileRequest;
import com.geekplay.userservice.model.User;
import com.geekplay.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
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
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        Optional<User> result = authService.authenticate("test@example.com", "password123");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void authenticate_WrongPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        Optional<User> result = authService.authenticate("test@example.com", "wrongPassword");

        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = authService.authenticate("notfound@example.com", "password123");

        assertFalse(result.isPresent());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_TrimsEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        Optional<User> result = authService.authenticate("  test@example.com  ", "password123");

        assertTrue(result.isPresent());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void register_Success() {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("plainPassword");
        newUser.setName("New User");

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        User result = authService.register(newUser);

        assertNotNull(result);
        assertEquals("hashedPassword", result.getPassword());
        verify(userRepository).findByEmail("newuser@example.com");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(newUser);
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");

        assertThrows(IllegalStateException.class, () -> authService.register(newUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_Success() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Updated Name");
        request.setPhone("987654321");
        request.setProfileImagePath("/images/new.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.updateProfile(1L, request);

        assertEquals("Updated Name", result.getName());
        assertEquals("987654321", result.getPhone());
        assertEquals("/images/new.jpg", result.getProfileImagePath());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_OnlyUpdateProvidedFields() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("Only Name Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.updateProfile(1L, request);

        assertEquals("Only Name Updated", result.getName());
        assertEquals("123456789", result.getPhone()); // Should remain unchanged
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_UserNotFound() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("New Name");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.updateProfile(999L, request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.changePassword(1L, "currentPassword", "newPassword");

        assertEquals("newHashedPassword", testUser.getPassword());
        verify(passwordEncoder).matches("currentPassword", "hashedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_IncorrectCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> authService.changePassword(1L, "wrongPassword", "newPassword"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> authService.changePassword(999L, "currentPassword", "newPassword"));
    }

    @Test
    void findUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = authService.findUserById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findUserByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.findUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void updateBanStatus_Success() {
        Long bannedUntil = System.currentTimeMillis() + 3600000;

        authService.updateBanStatus(1L, bannedUntil);

        verify(userRepository).updateBannedUntil(1L, bannedUntil);
    }
}
