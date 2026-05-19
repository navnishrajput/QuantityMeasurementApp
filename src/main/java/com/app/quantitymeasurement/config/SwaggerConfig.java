package com.app.quantitymeasurement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI quantityMeasurementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quantity Measurement API")
                        .description("REST API for Quantity Measurement Application - UC17 Spring Boot")
                        .version("1.0.0")
                        .contact(new Contact().name("Dev Team").email("dev@test.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
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