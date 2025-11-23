package com.geekplay.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // ✅ 1. REGLA MAESTRA PARA SWAGGER: Esto arregla el "Failed to load remote configuration"
                // Permite el acceso al JSON de la API y a los recursos de la UI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // ✅ 2. Endpoints Públicos de tu App
                .requestMatchers("/api/auth/**").permitAll() // Login/Register
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll() // Ver perfil
                .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll() // Editar perfil

                // 🔒 3. Todo lo demás bloqueado
                .anyRequest().authenticated()
            );
        return http.build();
    }
}