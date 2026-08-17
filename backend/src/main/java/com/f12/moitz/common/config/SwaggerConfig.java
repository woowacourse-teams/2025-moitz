package com.f12.moitz.common.config;

import com.f12.moitz.common.error.exception.ErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
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
                .version("2.0.0");

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info);
    }

    @Bean
    public OpenApiCustomizer customizer() {
        return openApi -> {
            if (openApi.getPaths() == null) return;

            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                    var responses = operation.getResponses();
                    if (path.equalsIgnoreCase("/recommendations")) {
                        injectExamplesForErrorEnum(path, httpMethod.name(), responses, 400, GeneralErrorCode.values());
                        injectExamplesForErrorEnum(path, httpMethod.name(), responses, 500, ExternalApiErrorCode.values());
                    }
                });
            });
        };
    }

    private void injectExamplesForErrorEnum(
            final String path,
            final String method,
            final ApiResponses responses,
            final int statusCode,
            final ErrorCode[] codes
    ) {
        final String statusKey = String.valueOf(statusCode);

        final ApiResponse apiResponse = responses.computeIfAbsent(
                statusKey,
                k -> new ApiResponse().description("자동 생성된 예외 응답")
        );

        Content content = apiResponse.getContent();
        if (content == null) {
            content = new Content();
            apiResponse.setContent(content);
        }

        final MediaType mediaType = content.computeIfAbsent(
                "application/json",
                k -> new MediaType()
        );

        Map<String, Example> examples = mediaType.getExamples();
        if (examples == null) {
            examples = new LinkedHashMap<>();
            mediaType.setExamples(examples);
        }

        for (ErrorCode code : codes) {
            final Example example = new Example();
            example.setDescription(code.getMessage());
            example.setValue(buildExampleResponse(path, method, statusCode, code));
            examples.put(code.getCode(), example);
        }

    }

    private Map<String, Object> buildExampleResponse(
            final String path,
            final String method,
            final int status,
            final ErrorCode code
    ) {
        return Map.of(
                "status", status,
                "code", code.getCode(),
                "message", code.getClientMessage(),
                "method", method,
                "path", path,
                "timestamp", LocalDateTime.now()
        );
    }

}
