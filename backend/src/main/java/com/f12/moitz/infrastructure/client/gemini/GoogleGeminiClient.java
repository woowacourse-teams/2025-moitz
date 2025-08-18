package com.f12.moitz.infrastructure.client.gemini;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.PromptGenerator;
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedPlaceResponses;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import com.google.genai.errors.ServerException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.f12.moitz.infrastructure.PromptGenerator.ADDITIONAL_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.RECOMMENDATION_COUNT;

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

    public List<PlaceRecommendResponse> generateWith(String prompt) {
        final GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.4F)
                .responseMimeType("application/json")
                .maxOutputTokens(5000)
                .build();

        GenerateContentResponse response = generateWith(prompt, config);
        return extractResponse(response).getPlacesByStationName();
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
            if (e.code() == HttpStatus.TOO_MANY_REQUESTS.value() || message.contains("exceeded your current quota")) {
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
        try {
            String originalText = generateContentResponse.candidates()
                    .map(candidates -> candidates.get(0))
                    .map(candidate -> candidate.content().orElse(null))
                    .map(content -> content.parts().orElse(null))
                    .map(parts -> parts.get(0))
                    .map(part -> part.text().orElse(null))
                    .orElse(null);

            if (originalText == null || originalText.trim().isEmpty()) {
                log.error("Gemini API 응답이 비어있습니다.");
                throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
            }

            String cleanedText = cleanJsonResponse(originalText);
            if (!isValidJson(cleanedText)) {
                log.error("유효하지 않은 JSON 형식: {}", cleanedText);
                throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
            }
            return readValue(cleanedText, RecommendedPlaceResponses.class);

        } catch (Exception e) {
            log.error("Gemini 응답 파싱 중 오류 발생", e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }

    /**
     * JSON 응답에서 마크다운 코드 블록과 불필요한 문자 제거
     */
    private String cleanJsonResponse(String text) {
        if (text == null) {
            return "";
        }

        // 1. 마크다운 코드 블록 제거 (```json, ```, ``` 등)
        String cleaned = text.replaceAll("```json\\s*", "")
                .replaceAll("```\\s*$", "")
                .replaceAll("^```\\s*", "")
                .trim();

        // 2. 앞뒤 공백 및 개행 문자 정리
        cleaned = cleaned.trim();

        // 3. JSON이 아닌 텍스트가 앞에 있는 경우 처리
        int jsonStart = findJsonStart(cleaned);
        if (jsonStart > 0) {
            cleaned = cleaned.substring(jsonStart);
        }

        // 4. JSON이 아닌 텍스트가 뒤에 있는 경우 처리
        int jsonEnd = findJsonEnd(cleaned);
        if (jsonEnd > 0 && jsonEnd < cleaned.length()) {
            cleaned = cleaned.substring(0, jsonEnd + 1);
        }

        return cleaned.trim();
    }

    /**
     * JSON 시작 위치 찾기 ({ 또는 [)
     */
    private int findJsonStart(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{' || c == '[') {
                return i;
            }
        }
        return 0;
    }

    /**
     * JSON 끝 위치 찾기 (} 또는 ])
     */
    private int findJsonEnd(String text) {
        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            switch (c) {
                case '{':
                    braceCount++;
                    break;
                case '}':
                    braceCount--;
                    if (braceCount == 0 && bracketCount == 0) {
                        return i;
                    }
                    break;
                case '[':
                    bracketCount++;
                    break;
                case ']':
                    bracketCount--;
                    if (braceCount == 0 && bracketCount == 0) {
                        return i;
                    }
                    break;
            }
        }
        return text.length() - 1;
    }

    /**
     * 기본적인 JSON 유효성 검사
     */
    private boolean isValidJson(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String trimmed = text.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    private <T> T readValue(final String content, final Class<T> valueType) {
        try {
            if (content == null || content.trim().isEmpty()) {
                throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
            }

            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. 내용: {}", content);
            log.error("파싱 오류 상세:", e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }
}
