package com.f12.moitz.infrastructure.gemini;

import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMEND_PROMPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GeminiFunctionCallerMockTest {

    @Mock
    private Client client;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    private GoogleGeminiClient geminiClient = new GoogleGeminiClient(client, objectMapper);

    @Mock
    private KakaoMapClient kakaoMapClient;

    private GeminiFunctionCaller functionCaller = new GeminiFunctionCaller(kakaoMapClient, objectMapper);

    @DisplayName("함수명이 올바르게 맵에 등록되는지 테스트")
    @Test
    void functionMappingTest() throws Exception {
        // given

        // when
        Field functionsField = GeminiFunctionCaller.class.getDeclaredField("functions");
        functionsField.setAccessible(true);
        Map<String, ?> functions = (Map<String, ?>) functionsField.get(functionCaller);

        // then
        assertThat(functions).containsKeys("getPointByPlaceName", "getPlacesByKeyword");
    }

    @DisplayName("함수 호출 요청이 없고, 응답 형식이 잘못된 경우 RetryableApiException(INVALID_GEMINI_RESPONSE_FORMAT)이 발생한다")
    @Test
    void functionCallingLoopEndTest() {
        // Given
        String targetPlaces = "잠실역(x=127.10023101886318, y=37.51331105877401), 삼성역(x=127.06302321147605, y=37.508822740225305)";
        String prompt = String.format(PLACE_RECOMMEND_PROMPT, PLACE_RECOMMENDATION_COUNT, targetPlaces, "곱창 맛집");

        GenerateContentResponse fakeResponse = GenerateContentResponse.builder()
                .candidates(Candidate.builder().content(Content.fromParts(Part.fromText("함수 호출 요청 없음"))).build())
                .build();

        doReturn(fakeResponse).when(geminiClient).generate(anyList(), any(GenerateContentConfig.class));

        // When & Then
        assertThatThrownBy(() -> functionCaller.generateWith(prompt, geminiClient))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
    }

    @DisplayName("등록되지 않은 이름의 함수 호출 요청이 올 경우 IllegalStateException이 발생한다")
    @Test
    void notExistfunctionCallingTest() {
        // Given
        String targetPlaces = "잠실역(x=127.10023101886318, y=37.51331105877401), 삼성역(x=127.06302321147605, y=37.508822740225305)";
        String prompt = String.format(PLACE_RECOMMEND_PROMPT, PLACE_RECOMMENDATION_COUNT, targetPlaces, "곱창 맛집");

        GenerateContentResponse fakeResponse = GenerateContentResponse.builder()
                .candidates(Candidate.builder()
                        .content(Content.fromParts(Part.fromFunctionCall("없는 이름", new HashMap<>())))
                        .build()
                )
                .build();

        doReturn(fakeResponse).when(geminiClient).generate(anyList(), any(GenerateContentConfig.class));

        // When & Then
        assertThatThrownBy(() -> functionCaller.generateWith(prompt, geminiClient))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("존재하지 않는 함수 이름");
    }

}
