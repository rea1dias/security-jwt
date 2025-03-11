package com.disa.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "auth-service",
                version = "1.0",
                description = "Документация API"
        ),
        servers = {
                @Server(url = "/", description = "Локальный сервер")
        }
)
public class OpenApiConfig {
}
