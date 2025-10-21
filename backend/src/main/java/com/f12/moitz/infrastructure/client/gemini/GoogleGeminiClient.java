package com.f12.moitz.infrastructure.client.gemini;

import static com.f12.moitz.infrastructure.PromptGenerator.ADDITIONAL_WITH_CANDIDATE_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.RECOMMENDATION_COUNT;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.PromptGenerator;
import com.f12.moitz.infrastructure.client.gemini.utils.JsonParser;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleGeminiClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";

    private final JsonParser jsonParser;
    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public RecommendedLocationsResponse generateResponse(
            final List<String> startingPlaces,
            final List<String> candidatePlaces,
            final List<String> requirements
    ) {
        return readValue(
                generateContent(
                        startingPlaces,
                        candidatePlaces,
                        requirements,
                        PromptGenerator.getSchema()
                ).text(),
                RecommendedLocationsResponse.class
        );
    }

    private GenerateContentResponse generateContent(
            final List<String> startingStations,
            final List<String> candidateStations,
            final List<String> requirements,
            final Map<String, Object> inputData
    ) {
        final String prompt = String.format(
                ADDITIONAL_WITH_CANDIDATE_PROMPT,
                RECOMMENDATION_COUNT,
                startingStations,
                candidateStations,
                requirements,
                RECOMMENDATION_COUNT
        );

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
            final String message = e.message();
            log.warn("Gemini API 호출 중 오류 발생: {}", message);
            if (e.code() == HttpStatus.FORBIDDEN.value() || message.contains("API key not valid")) {
                throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_KEY, e.getMessage());
            }
            if (e.code() == HttpStatus.TOO_MANY_REQUESTS.value() || message.contains("exceeded your current quota")) {
                throw new ExternalApiException(ExternalApiErrorCode.EXCEEDED_GEMINI_API_TOKEN_QUOTA, e.getMessage());
            }
            if (e.code() == HttpStatus.SERVICE_UNAVAILABLE.value() || message.contains("try again later")) {
                throw new RetryableApiException(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE, e.getMessage());
            }
            if (e.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new RetryableApiException(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE, e.getMessage());
            }
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE, e.getMessage());
        } catch (RuntimeException e) {
            log.error("Gemini API 호출 중 예기치 않은 오류 발생", e);
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE, e.getMessage());
        }
    }

    public GenerateContentResponse generate(final List<Content> contents, final GenerateContentConfig config) {
        final GenerateContentResponse generateContentResponse = geminiClient.models.generateContent(
                GEMINI_MODEL,
                contents,
                config
        );
        log.debug("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());

        return generateContentResponse;
    }

    private <T> T readValue(final String content, final Class<T> valueType) {
        try {
            if (content == null || content.trim().isEmpty()) {
                log.error("Gemini API 응답이 비어있습니다.");
                throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
            }

            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. 내용: {}", content, e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }

}
