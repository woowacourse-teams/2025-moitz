package com.f12.moitz.infrastructure.client.gemini;

import static com.f12.moitz.infrastructure.PromptGenerator.ADDITIONAL_WITH_CANDIDATE_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.RECOMMENDATION_COUNT;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.PromptGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("load-test")
@Primary
@RequiredArgsConstructor
@Slf4j
public class MockGeminiClient implements GeminiClient {

    private final RestClient mockGeminiRestClient;
    private final ObjectMapper objectMapper;

    @Override
    public GenerateContentResponse generate(final List<Content> contents, final GenerateContentConfig config) {
        log.info("Mock Gemini API 호출: localhost:8081/mock/gemini/generate");

        MockGeminiRequest request = new MockGeminiRequest(contents, config);

        GenerateContentResponse response = mockGeminiRestClient.post()
                .uri("/generate")
                .body(request)
                .retrieve()
                .body(GenerateContentResponse.class);

        log.debug("Mock Gemini 응답 성공");

        return response;
    }

    @Override
    public RecommendedLocationsResponse generateResponse(
            final List<String> startingPlaces,
            final List<String> candidatePlaces,
            final List<String> requirements
    ) {
        log.info("Mock Gemini API 호출 (generateResponse): localhost:8081/mock/gemini/generate");

        // GoogleGeminiClient와 동일한 방식으로 처리
        GenerateContentResponse geminiResponse = generateContent(
                startingPlaces,
                candidatePlaces,
                requirements,
                PromptGenerator.getSchema()
        );

        // Gemini API 응답 형식에서 text 추출하여 파싱
        return readValue(geminiResponse.text(), RecommendedLocationsResponse.class);
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

    private GenerateContentResponse generateWith(final String prompt, final GenerateContentConfig config) {
        return generate(List.of(Content.fromParts(Part.fromText(prompt))), config);
    }

    private <T> T readValue(final String content, final Class<T> valueType) {
        try {
            if (content == null || content.trim().isEmpty()) {
                log.error("Mock Gemini API 응답이 비어있습니다.");
                throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
            }

            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. 내용: {}", content, e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }

    private record MockGeminiRequest(
            List<Content> contents,
            GenerateContentConfig config
    ) {

    }

}
