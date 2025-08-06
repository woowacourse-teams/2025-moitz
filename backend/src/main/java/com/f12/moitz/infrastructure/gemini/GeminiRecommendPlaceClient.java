package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedPlaceResponses;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.kakao.dto.SearchPlacesRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.google.genai.types.Type.Known;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GeminiRecommendPlaceClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final int RECOMMENDATION_COUNT = 3;
    private static final String PLACE_RECOMMEND_PROMPT = """
            You're an AI assistant that recommends %d specific places within 800m of each given subway station.
   
            TASK OVERVIEW:
            You must complete this task in 2 phases:
            Phase 1: Data Collection (using function calls)
            Phase 2: Final Recommendation (generating JSON response)

            PHASE 1 - DATA COLLECTION:
            1. From the user requirements, extract search keywords that represent the types of places the user is looking for.
               - Each keyword must be in Korean and consist of only one word.
               - These keywords will be used with the getPlacesByKeyword function.
               - Among the search results, identify the top places based on their star ratings, as shown on the place URLs, and include their rankings using index values (e.g., 1 for the highest-rated place).

            2. For each (station, keyword) pair, create one getPlacesByKeyword function call. Do not send them one by one. Instead, batch all function calls together in a single request.
  
            PHASE 2 - FINAL RECOMMENDATION:
            After collecting all the data from function calls, you MUST generate the final recommendation.
   
            IMPORTANT: Even after receiving function call responses, you must continue to:
            1. Analyze all the collected place data
            2. For each station, select the top %d places based on:
                - Highest Star ratings
                - Most Reviews
                -Relevance to user requirements
            3. Assign index rankings (1 = best, 2 = second best, etc.)
            4. Generate the final JSON response
            
            CRITICAL OUTPUT FORMAT - READ CAREFULLY:
            Your FINAL response (after all function calls are complete) must be ONLY the JSON data with NO formatting whatsoever.
            
            Each recommendation must be returned strictly as raw JSON, with no surrounding text, explanation, or formatting
            
            
            Structure for your final response:
            {
                "responses": [
                    {
                        "stationName": "",
                        "places": [
                            {
                              "index": "",
                              "name": "",
                              "category": "",
                              "distance": "",
                              "url": ""
                            }
                        ]
                    }
                ]
            }
            
            Stations (지하철역): %s
            User Requirements (사용자 조건): %s
            
            EXECUTION INSTRUCTION:
            1. Start by calling the necessary functions to collect data
            2. After receiving all function responses, analyze the data and generate the final JSON recommendation
            3. Do NOT stop after function calls - you must provide the final recommendation JSON
            """;

    private final Client geminiClient;
    private final KakaoMapClient kakaoMapClient;
    private final ObjectMapper objectMapper;

    private final Map<String, Function<Object, Object>> functions = new HashMap<>();

    public GeminiRecommendPlaceClient(
            final @Autowired Client.Builder geminiClientBuilder,
            final @Autowired KakaoMapClient kakaoMapClient,
            final @Value("${gemini.api.key}") String apiKey,
            final @Autowired ObjectMapper objectMapper
    ) {
        this.geminiClient = geminiClientBuilder.apiKey(apiKey).build();
        this.kakaoMapClient = kakaoMapClient;
        this.objectMapper = objectMapper;

        functions.put("getPointByPlaceName", arg -> kakaoMapClient.searchPointBy((String) arg));
        functions.put("getPlacesByKeyword", arg -> kakaoMapClient.searchPlacesBy((SearchPlacesRequest) arg));
    }

    public Map<Place,List<PlaceRecommendResponse>> generateWithParallelFunctionCalling(Set<Place> places, String requirement) {
        List<Content> history = new ArrayList<>();
        String placeNames = places.stream()
                .map(place -> place.getName() + "(x=" + place.getPoint().getX() + ", y=" + place.getPoint().getY() + ")")
                .collect(Collectors.joining(", "));
        String prompt = String.format(PLACE_RECOMMEND_PROMPT, RECOMMENDATION_COUNT,RECOMMENDATION_COUNT, placeNames,requirement);
        history.add(Content.fromParts(Part.fromText(prompt)));

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.0F)
                .maxOutputTokens(5000)
                .tools(buildTool())
                .build();

        GenerateContentResponse generateContentResponse = geminiClient.models.generateContent(
                GEMINI_MODEL,
                history,
                config
        );
        log.info("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());

        // 응답에 FunctionCall이 있으면 반복
        while (!generateContentResponse.functionCalls().isEmpty()) {
            // Gemini 응답을 재요청 시 보낼 history에 추가
            history.add(generateContentResponse.candidates().get().getFirst().content().get());
            System.out.println("함수 호출 요청하는 응답: " + generateContentResponse.functionCalls());

            List<Part> kakaoResults = executeFunctionCalls(generateContentResponse);

            // FunctionResponse를 담은 Content 생성 및 history에 추가
            Content content = Content.builder().parts(kakaoResults).build();
            log.info("content 생성: {}", content.parts().get());
            history.add(content);

            generateContentResponse = geminiClient.models.generateContent(
                    GEMINI_MODEL,
                    history,
                    config
            );
            log.info("Gemini 응답 성공, 토큰 사용 {}개", generateContentResponse.usageMetadata().get().totalTokenCount().get());
        }
        // 최종 응답 출력
        System.out.println("응답: " + generateContentResponse.text());

        String requestText = generateContentResponse.text();

        String originalText = requestText.replaceAll("```json\\s*", "")
                .replaceAll("```\\s*$", "")
                .replaceAll("^```\\s*", "")
                .trim();

        System.out.println(originalText);
        Map<Place, List<PlaceRecommendResponse>> recommendPlacesResult = new HashMap<>();
        RecommendedPlaceResponses recommendedPlaceResponses;
        try {
            recommendedPlaceResponses = objectMapper.readValue(originalText, RecommendedPlaceResponses.class);
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
        places.forEach(place -> recommendPlacesResult.put(place, recommendedPlaceResponses.findPlacesByStationName(place.getName())));
        return recommendPlacesResult;
    }

    private List<Part> executeFunctionCalls(final GenerateContentResponse generateContentResponse) {
        List<Part> kakaoResults = new ArrayList<>();

        for (FunctionCall functionCall : generateContentResponse.functionCalls()) {
            String functionCallName = functionCall.name().get();
            System.out.println("Need to invoke function: " + functionCallName);

            if (functions.containsKey(functionCallName)) {
                Map<String, Object> functionCallParameter = functionCall.args().get();

                Object result;

                if ("getPointByPlaceName".equals(functionCallName)) {
                    result = functions.get(functionCallName)
                            .apply(functionCallParameter.values().stream().findFirst().orElse(null));

                    kakaoResults.add(Part.fromFunctionResponse(functionCallName, Map.of("point", result)));
                } else {
                    SearchPlacesRequest request = objectMapper.convertValue(functionCallParameter,
                            SearchPlacesRequest.class);

                    result = functions.get(functionCallName).apply(request);

                    kakaoResults.add(Part.fromFunctionResponse(functionCallName, Map.of("content", result)));
                }
            }
        }
        return kakaoResults;
    }

    private static Tool buildTool() {
        // Add the functions to a "tool"
        return Tool.builder()
                .functionDeclarations(
                        List.of(declareGetPointByPlaceNameFunction(), declareGetPlacesByKeywordFunction()))
                .build();
    }

    private static FunctionDeclaration declareGetPlacesByKeywordFunction() {
        // Declare the getPlacesByKeword function
        return FunctionDeclaration.builder()
                .name("getPlacesByKeyword")
                .description("get places by keyword within a specified radius (in meters) from the given coordinate")
                .parameters(
                        Schema.builder()
                                .type(Known.OBJECT)
                                .properties(Map.of(
                                        "query", Schema.builder().type(Known.STRING)
                                                .description("The keyword must be in Korean.").build(),
                                        "longitude",
                                        Schema.builder().type(Known.NUMBER).description("x좌표(경도)").minimum(124.0)
                                                .maximum(132.0).build(),
                                        "latitude",
                                        Schema.builder().type(Known.NUMBER).description("y좌표(위도)").minimum(33.0)
                                                .maximum(43.0).build(),
                                        "radius",
                                        Schema.builder().type(Known.INTEGER).description("반경").minimum(0.0)
                                                .maximum(20000.0).build()
                                )).required("query", "longitude", "latitude", "radius")
                                .build()
                ).build();
    }

    private static FunctionDeclaration declareGetPointByPlaceNameFunction() {
        // Declare the getPointByPlaceName function
        return FunctionDeclaration.builder()
                .name("getPointByPlaceName")
                .description("Get coordinate by the given place name")
                .parameters(
                        Schema.builder()
                                .type(Known.OBJECT)
                                .properties(Map.of(
                                        "placeName", Schema.builder()
                                                .type(Known.STRING)
                                                .description(
                                                        "The place name must be the Korean name of a subway station located in the Seoul Metropolitan Area of South Korea")
                                                .build()
                                )).required("placeName")
                                .build()
                ).build();
    }
}
