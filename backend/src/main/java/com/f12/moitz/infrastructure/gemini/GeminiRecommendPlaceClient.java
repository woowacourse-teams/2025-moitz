package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class GeminiRecommendPlaceClient {

    private static final String GEMINI_MODEL = "gemini-2.0-flash";
    private static final int RECOMMENDATION_COUNT = 3;

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

    public RecommendedPlaceResponses generateWithParallelFunctionCalling(final String prompt) {
        List<Content> history = new ArrayList<>();
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

        try {
            return objectMapper.readValue(originalText, RecommendedPlaceResponses.class);
        } catch (JsonProcessingException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
        }
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