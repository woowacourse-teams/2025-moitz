package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentConfig;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Component
@Slf4j
public class GoogleGeminiClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final int RECOMMENDATION_COUNT = 5;

    private static final String ADDITIONAL_PROMPT = """
        당신은 서울의 최적의 모임 장소를 추천하는 AI 비서입니다. 당신의 목표는 모든 출발지에서 지하철 이동 시간이 비슷하고, 사용자의 추가 조건을 만족하는 장소를 추천하는 것입니다.

        ## 핵심 임무:
        1.  **지하철 이동 시간 기준**: 모든 계산은 지하철을 기준으로 합니다.
        2.  **서울 지하철역 범위**: 출발지와 추천 장소는 모두 서울 지하철역이어야 합니다.
        3.  **유사한 이동 시간**: 추천 장소까지의 이동 시간 편차(가장 오래 걸리는 시간 - 가장 적게 걸리는 시간)는 15분 이내여야 합니다.
        4.  **편의 시설**: 추천 장소는 지하철역 근처에 식당/카페가 많아야 하며, 아래의 사용자 추가 조건을 반드시 만족해야 합니다.
        5.  **출발지 제외**: 추천 장소는 주어진 출발지 중 하나여서는 안 됩니다.

        ## 입력 정보:
        - 총 추천 장소 개수: %d개
        - 출발지 목록: %s
        - 사용자 추가 조건: %s

        위 조건에 맞춰 장소를 추천해주세요.
        """;

    private static final String ADDITIONAL_PROMPT2 = """
                    Given a list of Seoul Metro subway stations as input, recommend 3 meeting places (e.g. restaurant, coffee shop, shopping center) that satisfy the user condition for each subway station.
                    If the user condition is not specified, recommend 3 meeting places that are the most popular or well known and easily accessible from the subway stations.

                    Input:
                    Stations: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")

                    Output:
                    Provide the response in the structured JSON format defined by the provided schemas.
            """;

    private static final String ADDITIONAL_PROMPT3 = """
                    You're an AI assistant recommending optimal meeting locations in Seoul. Your main goal is to first suggest subway stations where subway travel times from all starting points are similar, and then recommend 3 places or facilities near those stations.

                    Core Conditions:
                    Subway Travel Only: Travel time calculations must be limited to public transportation (subway).
                    Subway Station Scope: Destination points must be limited to Seoul Metro subway stations.
                    Similar Travel Times: The travel time from each starting point to the recommended destination must be within a 15-minute margin of error (max_time - min_time <= 15 minutes) across all starting points.
                    Facility Sufficiency: Recommended areas must be near subway stations, have ample dining/cafes/convenience facilities, and specifically meet any additional user conditions.
                    Exclusion: The recommended locations must NOT be any of the provided Starting Points.

                    Recommendation Requirements:
                    Recommend a total of %d subway stations and 3 places near those stations.
                    For each subway station, provide the following detailed format per starting point: travelMethod, travelRoute, totalTimeInMinutes, travelCost, and numberOfTransfers.
                    Additionally, for each subway station, you must provide a concise, one-line summary reason (e.g., '접근성 좋고 맛집이 많아요! 😋') explaining why this specific location is recommended, highlighting its key advantages based on the user's conditions and travel similarities.
                    This reason MUST be very brief, strictly under 50 characters (including spaces and punctuation). Use emojis SPARINGLY, for example, 1-3 emojis at most, to enhance expressiveness, but do NOT include excessive or repetitive emojis.
                    Finally, recommend 3 places or facilities near each subway station that meet the user's additional conditions. These can be restaurants, cafes, or other relevant places.
                    Do NOT recommend stations or places that fail to meet the Additional User Condition.

                    Input:
                    Starting Points: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")

                    Output:
                    Provide the response in the structured JSON format defined by the provided schemas.
            """;

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public GoogleGeminiClient(
            final @Autowired Client.Builder geminiClientBuilder,
            final @Value("${gemini.api.key}") String apiKey,
            final @Autowired ObjectMapper objectMapper
    ) {
        this.geminiClient = geminiClientBuilder.apiKey(apiKey).build();
        this.objectMapper = objectMapper;
    }

    public RecommendedLocationResponse generateResponse(final List<String> stationNames, final String requirement) {
        try {
            return objectMapper.readValue(
                    generateContent(stationNames, requirement, getSchema()).text(),
                    RecommendedLocationResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }

    }

    private GenerateContentResponse generateContent(
            final List<String> stationNames,
            final String requirement,
            final Map<String, Object> inputData
    ) {
        final String stations = String.join(", ", stationNames);
        final String prompt = String.format(ADDITIONAL_PROMPT, RECOMMENDATION_COUNT, stations, requirement);
        final GenerateContentResponse generateContentResponse = generateBasicContent(
                GEMINI_MODEL,
                prompt,
                inputData
        );
        log.debug("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());
        return generateContentResponse;
    }

    private GenerateContentResponse generateBasicContent(String model, String prompt, Map<String, Object> inputData) {
        final GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.4F)
                .maxOutputTokens(5000)
                .responseMimeType("application/json")
                .responseJsonSchema(inputData)
                .build();

        return geminiClient.models.generateContent(
                model,
                prompt,
                config
        );
    }

    private Map<String, Object> getSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "description",
                                "추천된 장소들의 이름 리스트. 총 N개의 지하철역 이름(문자열)을 포함합니다.",
                                "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "locationName", Map.of("type", "string", "description", "추천 장소의 이름"),
                                                "reason", Map.of(
                                                        "type", "string",
                                                        "description", "해당 장소를 추천하는 간결한 한 줄 요약 이유 20자 이내, 어울리는 이모지 1개와 함께 (예: '접근성 좋고 맛집이 많아요! 😋') 만약 추천 이유에 사용자 조건이 포함되어 있다면, 이유에 명시할 것",
                                                        "maxLength", 20
                                                )
                                        ),
                                        "required", List.of("locationName", "reason")
                                ),
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }

}
