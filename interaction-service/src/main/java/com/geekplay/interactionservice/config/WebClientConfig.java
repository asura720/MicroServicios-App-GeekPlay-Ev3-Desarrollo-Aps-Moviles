package com.geekplay.interactionservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Asegúrese de que estas propiedades existan en application.properties
    @Value("${user.service.base-url}")
    private String userServiceBaseUrl;

    @Value("${inter.service.secret-key}")
    private String sharedSecretKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(userServiceBaseUrl)
            //  Agrega el encabezado secreto a TODAS las peticiones salientes
            .defaultHeader("X-API-SECRET", sharedSecretKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();
    }
}