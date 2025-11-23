package com.geekplay.userservice.service;

import com.geekplay.userservice.dto.UpdateProfileRequest;
import com.geekplay.userservice.model.User;
import com.geekplay.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    /**
     * Autentica un usuario comparando el password plano con el hash BCrypt.
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email.trim());

        // Verificar contraseña con BCrypt
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user; 
        } else {
            return Optional.empty(); 
        }
    }

    /**
     * Registra un nuevo usuario hasheando la contraseña con BCrypt.
     */
    @Transactional
    public User register(User newUser) throws IllegalStateException {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalStateException("El correo ya está registrado"); 
        }
        
        // Hashear con BCrypt
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword); 
        
        return userRepository.save(newUser);
    }
    
    // -----------------------------------------------------------------
    // Métodos de Gestión de Perfil
    // -----------------------------------------------------------------

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone());
        }
        if (request.getProfileImagePath() != null) {
            user.setProfileImagePath(request.getProfileImagePath());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Guardar nueva contraseña encriptada
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // -----------------------------------------------------------------
    // Métodos de utilidad para otros servicios
    // -----------------------------------------------------------------

    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id); 
    }
    
    @Transactional
    public void updateBanStatus(Long userId, Long bannedUntil) {
        userRepository.updateBannedUntil(userId, bannedUntil);
    }

    
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}