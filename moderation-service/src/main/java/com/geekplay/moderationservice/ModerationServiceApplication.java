package com.geekplay.moderationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Moderation Service API",
		version = "1.0",
		description = "Microservicio encargado de la moderación de contenido"
)
)
public class ModerationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModerationServiceApplication.class, args);
	}

}
