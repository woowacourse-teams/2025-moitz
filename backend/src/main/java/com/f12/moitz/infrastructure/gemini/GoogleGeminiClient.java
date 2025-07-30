package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.infrastructure.gemini.dto.RecommendationsResponse;
import com.f12.moitz.infrastructure.gemini.dto.BriefRecommendedLocationResponse;
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
    private static final String BASIC_PROMPT = """
                    Purpose: Recommend meeting locations where subway travel times from all starting points are similar and distances are not too far.
                    Conditions: Travel time is limited to public transportation, and starting/destination points are limited to subway stations provided by the Seoul Metro. Travel time from each starting point to the destination should be within a 15-minute margin of error.
                    Recommended areas must be within the range of subway stations provided by the Seoul Metro and should have sufficient dining, cafes, and convenience facilities based on collected data (e.g., Naver Blog, Instagram, YouTube).
                    A total of %d areas must be recommended.
                    The recommendation must include the following format for each starting point: travel method, travel route, travel time, travel cost, and number of transfers. Must satisfy additional conditions provided by the user.
                    Starting Points:%s
                    Additional User Condition:%s
            """;
    private static final String ADDITIONAL_PROMPT = """
                    You're an AI assistant recommending optimal meeting locations in Seoul. Your main goal is to suggest places where subway travel times from all starting points are similar and distances aren't too far.

                    Core Conditions:
                    Subway Travel Only: Travel time calculations must be limited to public transportation (subway).
                    Subway Station Scope: Starting and destination points must be limited to Seoul Metro subway stations.
                    Similar Travel Times: The travel time from each starting point to the recommended destination must be within a 15-minute margin of error (max_time - min_time <= 15 minutes) across all starting points.
                    Facility Sufficiency: Recommended areas must be near subway stations, have ample dining/cafes/convenience facilities, and specifically meet any additional user conditions.
                    **Exclusion: The recommended locations must NOT be any of the provided Starting Points.** // 이 줄을 추가합니다.

                    Recommendation Requirements:
                    Recommend a total of %d locations.
                    For each recommended location, provide the following detailed format per starting point: travelMethod, travelRoute, totalTimeInMinutes, travelCost, and numberOfTransfers.
                    Additionally, for each recommended location, you must provide a concise, one-line summary reason (e.g., '접근성 좋고 맛집이 많아요! 😋') explaining why this specific location is recommended, highlighting its key advantages based on the user's conditions and travel similarities.
                    This reason MUST be very brief, strictly under 50 characters (including spaces and punctuation). Use emojis SPARINGLY, for example, 1-3 emojis at most, to enhance expressiveness, but do NOT include excessive or repetitive emojis.
                    Do NOT recommend locations that fail to meet the Additional User Condition.

                    Input:
                    Starting Points: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")

                    Kakao Category Extraction:
                    Analyze the Additional User Condition to extract relevant Kakao Local API Category Group Codes. Include all clearly mapping codes. If a condition doesn't clearly map, return ALL available Kakao Category Group Codes from the reference list.

                    Kakao Local API Category Group Codes (you must use these):
                    CT1: Cultural Facility
                    AT4: Tourist Attraction
                    AD5: Accommodation
                    FD6: Restaurant
                    CE7: Cafe

                    Based on analysis, you must explicitly include a list of relevant Kakao Category Group Codes in your response for the requirementsCategoryCodes field.

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

    public RecommendationsResponse generateDetailResponse(final List<String> stationNames, final String requirement) {
        try {
            return objectMapper.readValue(
                    generateContent(stationNames, requirement, getDetailSchema()).text(),
                    RecommendationsResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
    }

    public BriefRecommendedLocationResponse generateBriefResponse(final List<String> stationNames, final String requirement) {
        try {
            return objectMapper.readValue(
                    generateContent(stationNames, requirement, getBriefSchema()).text(),
                    BriefRecommendedLocationResponse.class
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
        log.info("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());
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

    private Map<String, Object> getDetailSchema() {
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
                        "requirementsMet", Map.of(
                                "type", "string",
                                "description",
                                "사용자의 추가 요구사항(PC방, 코인노래방 등) 충족 여부 및 확인 출처 (예: 'PC방 3곳, 코인노래방 2곳 확인됨 (네이버 블로그, 인스타그램 기준)'). 충족되지 않으면 추천하지 않을 것."
                        )
                ),
                "required", List.of("locationName", "movingInfos", "requirementsMet")
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
                        ),
                        "requirementsCategoryCodes", Map.of(
                                "type", "array",
                                "description", "사용자의 추가 요구사항에 매핑되는 카카오 로컬 API 카테고리 그룹 코드 리스트 (예: ['CT1', 'FD6']). 매핑되지 않으면 모두 포함.",
                                "items", Map.of("type", "string"),
                                "minItems", 0
                        )
                ),
                "required", List.of("recommendations", "requirementsCategoryCodes")
        );
    }

    private Map<String, Object> getBriefSchema() {
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
                                                        "description", "해당 장소를 추천하는 간결한 한 줄 요약 이유 50자 이내 (예: '접근성 좋고 맛집이 많아요!')",
                                                        "maxLength", 50
                                                )
                                        ),
                                        "required", List.of("locationName", "reason")
                                ),
                                "minItems", 3,
                                "maxItems", 5
                        ),
                        "requirementsCategoryCodes", Map.of(
                                "type", "array",
                                "description", "사용자의 추가 요구사항에 매핑되는 카카오 로컬 API 카테고리 그룹 코드 리스트 (예: ['CT1', 'FD6']). 매핑되지 않으면 모두 포함.",
                                "items", Map.of("type", "string"),
                                "minItems", 0,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations", "requirementsCategoryCodes")
        );
    }

}
