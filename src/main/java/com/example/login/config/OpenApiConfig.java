package com.example.login.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI loginOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Login API")
                        .description("REST API for user login and password reset")
                        .version("v1")
                        .contact(new Contact().name("Login API Support").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
