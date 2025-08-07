package com.f12.moitz.infrastructure.gemini;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.kakao.dto.SearchPlacesRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
import com.google.genai.types.Type.Known;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeminiFunctionCaller {

    private final KakaoMapClient kakaoMapClient;
    private final ObjectMapper objectMapper;
    private final Map<String, BiFunction<String, Map<String, Object>, Part>> functions = new HashMap<>();

    public GeminiFunctionCaller(final KakaoMapClient kakaoMapClient, final ObjectMapper objectMapper) {
        this.kakaoMapClient = kakaoMapClient;
        this.objectMapper = objectMapper;
        initializeFunctions();
    }

    private void initializeFunctions() {
        functions.put("getPointByPlaceName", this::executeGetPointByPlaceName);
        functions.put("getPlacesByKeyword", this::executeGetPlacesByKeyword);
    }

    private Part executeGetPointByPlaceName(final String functionName, final Map<String, Object> parameters) {
        String request = objectMapper.convertValue(parameters, String.class);

        Point point = kakaoMapClient.searchPointBy(request);

        return Part.fromFunctionResponse(functionName, Map.of("point", point));
    }

    private Part executeGetPlacesByKeyword(final String functionName, final Map<String, Object> parameters) {
        SearchPlacesRequest request = objectMapper.convertValue(parameters, SearchPlacesRequest.class);

        KakaoApiResponse response = kakaoMapClient.searchPlacesBy(request);

        return Part.fromFunctionResponse(functionName, Map.of("content", response));
    }

    public Map<String, List<PlaceRecommendResponse>> generateWith(
            final String prompt,
            final GoogleGeminiClient geminiClient
    ) {
        final GenerateContentConfig config = configureForFunctionCalling();

        final List<Content> history = new ArrayList<>();
        history.add(Content.fromParts(Part.fromText(prompt)));

        GenerateContentResponse generateContentResponse = geminiClient.generateWith(history, config);

        while (hasFunctionCallRequest(generateContentResponse)) {
            List<FunctionCall> functionCalls = generateContentResponse.functionCalls();
            log.debug("함수 호출 요청하는 응답: {}", functionCalls);

            history.add(generateContentResponse.candidates().get().getFirst().content()
                    .orElseThrow(() -> new IllegalStateException("함수 호출 요청 content가 존재하지 않습니다."))
            );

            final Content content = Content.builder()
                    .parts(execute(functionCalls))
                    .build();
            log.debug("함수 호출 결과 content 생성: {}", content.parts()
                    .orElseThrow(() -> new IllegalStateException("함수 호출 결과 content 생성 실패"))
            );

            history.add(content);

            generateContentResponse = geminiClient.generateWith(history, config);
        }

        log.debug("최종 응답: {}", generateContentResponse.text());

        return geminiClient.extractResponse(generateContentResponse).getPlacesByStationName();
    }

    private static boolean hasFunctionCallRequest(final GenerateContentResponse generateContentResponse) {
        List<FunctionCall> functionCalls = generateContentResponse.functionCalls();
        return functionCalls != null && !functionCalls.isEmpty();
    }

    public List<Part> execute(List<FunctionCall> functionCalls) {
        List<Part> parts = new ArrayList<>();

        functionCalls.forEach(functionCall -> parts.add(execute(functionCall)));

        return parts;
    }

    public Part execute(final FunctionCall functionCall) {
        String functionName = functionCall.name()
                .orElseThrow(() -> new IllegalStateException("함수 호출 요청이 존재하지 않습니다."));

        if (functions.containsKey(functionName)) {
            log.debug("함수 호출 필요: {}", functionName);

            Map<String, Object> functionArgs = functionCall.args()
                    .orElseThrow(() -> new IllegalStateException("호출할 함수 파라미터가 존재하지 않습니다."));

            return functions.get(functionName).apply(functionName, functionArgs);
        }

        throw new IllegalStateException("존재하지 않는 함수 이름: " + functionName);
    }

    private GenerateContentConfig configureForFunctionCalling() {
        return GenerateContentConfig.builder()
                .temperature(0.0F)
                .maxOutputTokens(5000)
                .tools(Tool.builder().functionDeclarations(getFunctionDeclarations()).build())
                .build();
    }

    private List<FunctionDeclaration> getFunctionDeclarations() {
        return List.of(declareGetPointByPlaceNameFunction(), declareGetPlacesByKeywordFunction());
    }

    private FunctionDeclaration declareGetPointByPlaceNameFunction() {
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

    private FunctionDeclaration declareGetPlacesByKeywordFunction() {
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

}
