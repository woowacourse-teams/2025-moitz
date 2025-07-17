package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.infrastructure.gemini.dto.RecommendationsResponse;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationPreview;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentConfig;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Component
@Log4j2
public class GoogleGeminiClient {

    private static final int RECOMMENDATION_COUNT = 5;
    private static final String BASIC_PROMPT = """
                    Purpose: Recommend meeting locations where subway travel times from all starting points are similar and distances are not too far.
                    Conditions: Travel time is limited to public transportation, and starting/destination points are limited to subway stations provided by the Seoul Metro. Travel time from each starting point to the destination should be within a 15-minute margin of error.
                    Recommended areas must be within the range of subway stations provided by the Seoul Metro and should have sufficient dining, cafes, and convenience facilities based on collected data (e.g., Naver Blog, Instagram, YouTube).
                    A total of %d areas must be recommended.
                    The recommendation must include the following format for each starting point: travel method, travel route, travel time, travel cost, and number of transfers. Must satisfy additional conditions provided by the user.
                    Starting Points:%s
                    Additional User Condition:%s
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

    public RecommendationsResponse generateDetailResponse(final String stations, final String additionalCondition) {
        try {
            return objectMapper.readValue(
                    generateContent(stations, additionalCondition, generateDetailData()).text(),
                    RecommendationsResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Gemini 응답 파싱 실패", e);
        }
    }

    public RecommendedLocationPreview generateBriefResponse(final String stations, final String additionalCondition) {
        try {
            return objectMapper.readValue(
                    generateContent(stations, additionalCondition, generateBriefData()).text(),
                    RecommendedLocationPreview.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Gemini 응답 파싱 실패", e);
        }

    }

    private GenerateContentResponse generateContent(
            final String stations,
            final String additionalCondition,
            final Map<String, Object> inputData
    ) {
        String prompt = String.format(BASIC_PROMPT, RECOMMENDATION_COUNT, stations, additionalCondition);
        final GenerateContentResponse generateContentResponse = generateBasicContent(
                "gemini-1.5-flash",
                prompt,
                inputData
        );
        log.info("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());
        return generateContentResponse;
    }

    private GenerateContentResponse generateBasicContent(String model, String prompt, Map<String, Object> inputData) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.5F)
                .maxOutputTokens(2000)
                .responseMimeType("application/json")
                .responseJsonSchema(inputData)
                .build();

        return geminiClient.models.generateContent(
                model,
                prompt,
                config
        );
    }

    private Map<String, Object> generateDetailData() {
        Map<String, Object> movingInfoSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "startStationName", Map.of("type", "string", "description", "출발 지하철역 이름"),
                        "travelMethods", Map.of(
                                "type", "array",
                                "description", "목적지까지 이용한 주요 대중교통 수단 및 환승 정보 (예: 지하철 5호선, 2호선 환승). 각 수단은 간결하게 표현",
                                "items", Map.of(
                                        "type", "string",
                                        "maxLength", 50
                                ),
                                "maxItems", 3
                        ),
                        "travelRoute", Map.of("type", "string", "description", "상세 이동 경로 (예: 강동역 → 천호역(환승) → 강남역)"),
                        "totalTimeInMinutes", Map.of("type", "integer", "description", "총 이동 시간 (분 단위)"),
                        "travelCost", Map.of("type", "string", "description", "예상 이동 비용 (예: 1,450 KRW)"),
                        "numberOfTransfers", Map.of("type", "integer", "description", "총 환승 횟수")
                ),
                "required",
                List.of("startStationName", "travelMethods", "travelRoute", "totalTimeInMinutes", "travelCost",
                        "numberOfTransfers")
        );

        Map<String, Object> recommendedLocationSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "locationName", Map.of("type", "string", "description", "추천 장소의 이름 (예: 강남역)"),
                        "movingInfos", Map.of(
                                "type", "array",
                                "description", "각 출발점에서 해당 장소까지의 이동 정보 리스트. 각 출발점에 대한 하나의 MovingInfo 객체 포함",
                                "items", movingInfoSchema
                        ),
                        "additionalConditionsMet", Map.of(
                                "type", "string",
                                "description",
                                "사용자의 추가 요구사항(PC방, 코인노래방 등) 충족 여부 및 확인 출처 (예: 'PC방 3곳, 코인노래방 2곳 확인됨 (네이버 블로그, 인스타그램 기준)'). 충족되지 않으면 추천하지 않을 것."
                        )
                ),
                "required", List.of("locationName", "movingInfos", "additionalConditionsMet")
        );

        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "description", "추천된 장소들의 리스트. 총 N개의 RecommendedLocation 객체 포함",
                                "items", recommendedLocationSchema,
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }

    private Map<String, Object> generateBriefData() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "description",
                                "추천된 장소들의 이름 리스트. 총 N개의 지하철역 이름(문자열)을 포함합니다.",
                                "items", Map.of("type", "string"),
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }

}
