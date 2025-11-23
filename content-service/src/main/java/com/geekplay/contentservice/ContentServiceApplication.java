package com.geekplay.contentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Content Service API",
        version = "1.0",
        description = "Microservicio encargado del contenido"
)
)
public class ContentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentServiceApplication.class, args);
	}

}
