package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.infrastructure.gemini.dto.RecommendationPlaceResponse;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoogleGeminiClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final int RECOMMENDATION_COUNT = 5;

    private static final String ADDITIONAL_PROMPT = """
                    You're an AI assistant recommending optimal meeting locations in Seoul. Your main goal is to suggest places where subway travel times from all starting points are similar and distances aren't too far.

                    Core Conditions:
                    Subway Travel Only: Travel time calculations must be limited to public transportation (subway).
                    Subway Station Scope: Starting and destination points must be limited to Seoul Metro subway stations.
                    Similar Travel Times: The travel time from each starting point to the recommended destination must be within a 15-minute margin of error (max_time - min_time <= 15 minutes) across all starting points.
                    Facility Sufficiency: Recommended areas must be near subway stations, have ample dining/cafes/convenience facilities, and specifically meet any additional user conditions.
                    Exclusion: The recommended locations must NOT be any of the provided Starting Points.

                    Recommendation Requirements:
                    Recommend a total of %d locations.
                    For each recommended location, provide the following detailed format per starting point: travelMethod, travelRoute, totalTimeInMinutes, travelCost, and numberOfTransfers.
                    Additionally, for each recommended location, you must provide a concise, one-line summary reason (e.g., '접근성 좋고 맛집이 많아요! 😋') explaining why this specific location is recommended, highlighting its key advantages based on the user's conditions and travel similarities.
                    This reason MUST be very brief, strictly under 50 characters (including spaces and punctuation). Use emojis SPARINGLY, for example, 1-3 emojis at most, to enhance expressiveness, but do NOT include excessive or repetitive emojis.
                    Do NOT recommend locations that fail to meet the Additional User Condition.

                    Input:
                    Starting Points: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")

                    Output:
                    Provide the response in the structured JSON format defined by the provided schemas.
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
                    Additional User Condition: %s

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

    public RecommendedLocationResponse generateLocationResponse(final List<String> stationNames, final String requirement) {
        try {
            return objectMapper.readValue(
                    generateContent(ADDITIONAL_PROMPT, stationNames, requirement, getLocationSchema()).text(),
                    RecommendedLocationResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }

    }

    public RecommendationPlaceResponse generatePlaceResponse(
            final List<String> stationNames,
            final String requirement
    ) {
        try {
            return objectMapper.readValue(
                    generateContent(ADDITIONAL_PROMPT2, stationNames, requirement, getPlaceSchema()).text(),
                    RecommendationPlaceResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }

    }

    private GenerateContentResponse generateContent(
            final String prompt,
            final List<String> stationNames,
            final String requirement,
            final Map<String, Object> inputData
    ) {
        final String stations = String.join(", ", stationNames);
        final String formatedPrompt = String.format(prompt, RECOMMENDATION_COUNT, stations, requirement);
        final GenerateContentResponse generateContentResponse = generateBasicContent(
                GEMINI_MODEL,
                formatedPrompt,
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

    private Map<String, Object> getLocationSchema() {
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
                                                        "description",
                                                        "해당 장소를 추천하는 간결한 한 줄 요약 이유 50자 이내 (예: '접근성 좋고 맛집이 많아요!')",
                                                        "maxLength", 50
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

    private Map<String, Object> getPlaceSchema() {
        Map<String, Object> placeSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "name", Map.of(
                                "type", "string",
                                "description", "추천 장소의 이름 (예: 스타벅스 강남역점)"
                        ),
                        "category", Map.of(
                                "type", "string",
                                "description", "장소의 카테고리 (예: 카페, 음식점, 쇼핑몰)"
                        ),
                        "description", Map.of(
                                "type", "string",
                                "description", "해당 장소를 추천하는 이유나 간단한 설명"
                        ),
                        "address", Map.of(
                                "type", "string",
                                "description", "상세 주소"
                        ),
                        "latitude", Map.of(
                                "type", "number",
                                "description", "위도 좌표"
                        ),
                        "longitude", Map.of(
                                "type", "number",
                                "description", "경도 좌표"
                        )
                ),
                "required", List.of("name", "category", "description", "address", "latitude", "longitude")
        );

        Map<String, Object> stationRecommendationSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "stationName", Map.of(
                                "type", "string",
                                "description", "지하철역 이름 (예: 강남역)"
                        ),
                        "places", Map.of(
                                "type", "array",
                                "description", "해당 지하철역에 대해 추천하는 3개의 장소 목록",
                                "items", placeSchema,
                                "minItems", 3,
                                "maxItems", 3
                        )
                ),
                "required", List.of("stationName", "places")
        );

        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "stationRecommendations", Map.of(
                                "type", "array",
                                "description", "입력된 각 지하철역에 대한 추천 장소 목록",
                                "items", stationRecommendationSchema
                        )
                ),
                "required", List.of("stationRecommendations")
        );
    }

}
