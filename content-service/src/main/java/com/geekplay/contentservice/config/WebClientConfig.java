package com.geekplay.contentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${user.service.base-url}")
    private String userServiceBaseUrl;

    @Value("${inter.service.secret-key}")
    private String sharedSecretKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(userServiceBaseUrl)
            .defaultHeader("X-API-SECRET", sharedSecretKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();
    }
}