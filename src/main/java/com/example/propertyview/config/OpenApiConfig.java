package com.example.propertyview.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Property View API",
                version = "1.0.0",
                description = "REST API for viewing and searching hotel properties"
        ),
        servers = {
                @Server(url = "/property-view", description = "Property View API base path")
        }
)
public class OpenApiConfig {
}

