package com.f12.moitz.infrastructure.gemini;

import static com.f12.moitz.infrastructure.PromptGenerator.ADDITIONAL_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.RECOMMENDATION_COUNT;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.PromptGenerator;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedPlaceResponses;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import com.google.genai.errors.ServerException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleGeminiClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public RecommendedLocationResponse generateResponse(
            final List<String> stationNames,
            final String requirement
    ) {
        return readValue(
                generateContent(stationNames, requirement, PromptGenerator.getSchema()).text(),
                RecommendedLocationResponse.class
        );
    }

    private GenerateContentResponse generateContent(
            final List<String> stationNames,
            final String requirement,
            final Map<String, Object> inputData
    ) {
        final String stations = String.join(", ", stationNames);
        final String prompt = String.format(ADDITIONAL_PROMPT, RECOMMENDATION_COUNT, stations, requirement);

        final GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.4F)
                .maxOutputTokens(5000)
                .responseMimeType("application/json")
                .responseJsonSchema(inputData)
                .build();

        return generateWith(prompt, config);
    }

    public GenerateContentResponse generateWith(final String prompt, final GenerateContentConfig config) {
        return generateWith(List.of(Content.fromParts(Part.fromText(prompt))), config);
    }

    public GenerateContentResponse generateWith(final List<Content> contents, final GenerateContentConfig config) {
        try {
            return generate(contents, config);
        } catch (ClientException | ServerException e) {
            String message = e.message();
            if (e.code() == HttpStatus.FORBIDDEN.value() || message.contains("API key not valid")) {
                throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_KEY, e.getMessage());
            }
            if (e.code() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new ExternalApiException(ExternalApiErrorCode.EXCEEDED_GEMINI_API_TOKEN_QUOTA, e.getMessage());
            }
            if (e.code() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                throw new ExternalApiException(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE, e.getMessage());
            }
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE, e.getMessage());
        }
    }

    protected GenerateContentResponse generate(final List<Content> contents, final GenerateContentConfig config) {
        final GenerateContentResponse generateContentResponse = geminiClient.models.generateContent(
                GEMINI_MODEL,
                contents,
                config
        );
        log.debug("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());

        return generateContentResponse;
    }

    public RecommendedPlaceResponses extractResponse(final GenerateContentResponse generateContentResponse) {
        final String originalText = generateContentResponse.text()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*$", "")
                .replaceAll("^```\\s*", "")
                .trim();

        return readValue(originalText, RecommendedPlaceResponses.class);
    }

    private <T> T readValue(final String content, final Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }

}
