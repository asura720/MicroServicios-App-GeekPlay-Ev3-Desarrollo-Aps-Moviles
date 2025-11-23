package com.geekplay.userservice.config;

import com.geekplay.userservice.model.User;
import com.geekplay.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si ya existe el admin para no duplicarlo
        if (userRepository.findByEmail("admin@geekplay.cl").isEmpty()) {
            
            User admin = new User();
            admin.setName("Admin Geek");
            admin.setEmail("admin@geekplay.cl");
            admin.setPhone("123456789");
            
            // 🔒 La contraseña se guarda encriptada con BCrypt
            admin.setPassword(passwordEncoder.encode("Admin123!")); 
            
            // 🖼️ CLAVE: Usamos "res:spiderman" para que la App use su imagen local
            // (Asegúrate de que "spiderman" esté en tu ImageMapper.kt en Android)
            admin.setProfileImagePath("res:spiderman");
            
            // 👑 Le damos permisos de administrador
            admin.setAdmin(true); 
            
            userRepository.save(admin);
            
            System.out.println("✅ Usuario Administrador creado: admin@geekplay.cl");
        }
    }
}