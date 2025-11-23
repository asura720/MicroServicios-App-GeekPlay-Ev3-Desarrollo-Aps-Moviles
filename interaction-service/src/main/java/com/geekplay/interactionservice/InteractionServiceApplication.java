package com.geekplay.interactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Interaction Service API",
		version = "1.0",
		description = "Microservicio encargado de la interacción con el contenido"
)
)
public class InteractionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteractionServiceApplication.class, args);
	}

}
