package com.geekplay.interactionservice.service;

import com.geekplay.interactionservice.dto.UserClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Optional;

@Service
public class UserServiceClient {

    @Autowired // ⬅️ WebClient inyectado desde WebClientConfig
    private WebClient webClient;

    /**
     * Llama al User Service (8083) para obtener el nombre y foto de perfil por ID de forma segura.
     */
    public Optional<UserClientResponse> getUserDetails(Long userId) {
        try {
            // Llama a GET http://localhost:8083/api/users/{userId} con el encabezado secreto
            return webClient.get()
                .uri("/{id}", userId)
                .retrieve()
                .bodyToMono(UserClientResponse.class)
                .blockOptional(); // Bloquea y espera la respuesta (necesario en un servicio síncrono como JPA)
        } catch (Exception e) {
            System.err.println("Error al obtener detalles del usuario " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    

    public Optional<UserClientResponse> getUserDetailsByEmail(String email) {
        try {
            return webClient.get()
                .uri("/email/{email}", email) // Llama al nuevo endpoint del User Service
                .retrieve()
                .bodyToMono(UserClientResponse.class)
                .blockOptional();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}