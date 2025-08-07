package com.f12.moitz.common.config;

import com.f12.moitz.common.error.exception.ErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

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

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info);
    }

    @Bean
    public OpenApiCustomizer customizer() {
        return openApi -> {
            if(openApi.getPaths() == null) return;

            for (PathItem pathItem : openApi.getPaths().values()) {
                for (Operation operation : pathItem.readOperations()) {
                    ApiResponses responses = operation.getResponses();
                    injectExamplesForErrorEnum(responses, 400, GeneralErrorCode.values());
                    injectExamplesForErrorEnum(responses, 500, ExternalApiErrorCode.values());
                }
            }
        };
    }

    private void injectExamplesForErrorEnum(final ApiResponses responses, final int statusCode, final ErrorCode[] codes) {
        String statusKey = String.valueOf(statusCode);

        ApiResponse apiResponse = responses.computeIfAbsent(
                statusKey,
                k -> new ApiResponse().description("자동 생성된 예외 응답")
        );

        Content content = apiResponse.getContent();
        if (content == null) {
            content = new Content();
            apiResponse.setContent(content);
        }

        MediaType mediaType = content.computeIfAbsent(
                "*/*",
                k -> new MediaType()
        );

        Map<String, Example> examples = mediaType.getExamples();
        if (examples == null) {
            examples = new LinkedHashMap<>();
            mediaType.setExamples(examples);
        }

        for (ErrorCode code : codes) {
            Example example = new Example();
            example.setDescription(code.getMessage());
            example.setValue(buildExampleResponse(statusCode, code));
            examples.put(code.getCode(), example);
        }

    }

    private Map<String, Object> buildExampleResponse(final int status, final ErrorCode code) {
        return Map.of(
                "status", status,
                "code", code.getCode(),
                "message", code.getClientMessage(),
                "method", "POST",
                "path", "/example",
                "timestamp", "2025-08-07T12:00:00"
        );
    }


}
