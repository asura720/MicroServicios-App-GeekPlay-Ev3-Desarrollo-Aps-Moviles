package com.geekplay.interactionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/interactions/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/interactions/comments").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/interactions/posts/*/likes/toggle").permitAll()

                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                
                
                // ✅ NUEVA LÍNEA: Permitir borrar comentarios (necesario para el Moderation Service y el Usuario)
                .requestMatchers(HttpMethod.DELETE, "/api/interactions/**").permitAll()

                .anyRequest().authenticated()
            );
        return http.build();
    }

    // Nota: Este servicio asume que la autenticación (BCrypt) la realiza el User
    // Service (8083).
}