package com.app.quantitymeasurement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI quantityMeasurementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quantity Measurement API - Secured")
                        .description("REST API for Quantity Measurement Application with Google OAuth 2.0 & JWT Authentication")
                        .version("2.0.0")
                        .contact(new Contact().name("Dev Team").email("dev@test.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token obtained from Google OAuth2 login")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("quantity-api")
                .packagesToScan("com.app.quantitymeasurement.controller")
                .pathsToMatch("/api/**")
                .build();
    }
}