package com.f12.moitz.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI moitzOpenAPI() {
        final String title = "Moitz API Docs";
        final String description = "모잇지 API 문서입니다.";

        final Info info = new Info()
                .title(title)
                .description(description)
                .version("1.0.0");

        return new OpenAPI().info(info);
    }

}
