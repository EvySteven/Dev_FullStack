package com.depeche226.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Je centralise ici les informations visibles dans Swagger et OpenAPI.
public class OpenApiConfig {

    @Bean
    public OpenAPI depeche226OpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Depeche226 API")
                .description("Backend API for the Depeche226 editorial platform")
                .version("v1.0"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token issued after login")));
    }
}
