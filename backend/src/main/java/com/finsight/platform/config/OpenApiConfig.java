package com.finsight.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI finsightOpenApi() {
        return new OpenAPI().info(
                new Info()
                        .title("FinSight AI API")
                        .version("v1")
                        .description("Enterprise Incident Intelligence Platform API")
        );
    }
}
