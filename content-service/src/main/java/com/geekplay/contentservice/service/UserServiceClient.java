package com.geekplay.contentservice.service;

import com.geekplay.contentservice.dto.UserClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class UserServiceClient {

    @Autowired
    private WebClient webClient;

    public Optional<UserClientResponse> getUserDetails(Long userId) {
        try {
            return webClient.get()
                .uri("/{id}", userId)
                .retrieve()
                .bodyToMono(UserClientResponse.class)
                .blockOptional();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}