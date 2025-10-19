package com.f12.moitz.infrastructure.client.gemini;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.PromptGenerator;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.errors.ClientException;
import com.google.genai.errors.ServerException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleGeminiClientTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    @Spy
    private GoogleGeminiClient googleGeminiClient;

    @DisplayName("generateResponse() 메서드가 성공적으로 RecommendedLocationResponse를 반환한다")
    @Test
    void generateResponseSuccessTest() throws JsonProcessingException {
        // Given
        final List<String> stationNames = List.of("강남역", "홍대입구역");
        final List<String> candidateNames = List.of("이태원역", "신사역", "동작역", "신용산역", "영등포구청역");
        final List<String> requirement = List.of("맛집이 많은 곳");

        final String expectedJsonResponse = """
                {
                    "recommendations": [
                        {
                            "locationName": "신촌역",
                            "reason": "접근성 좋고 맛집이 많아요! 😋"
                        },
                        {
                            "locationName": "이대역",
                            "reason": "학생들이 많아 맛집이 많아요! 🍜"
                        }
                    ]
                }
                """;

        final RecommendedLocationsResponse expectedResponse = new RecommendedLocationsResponse(
                List.of(
                        new RecommendedLocationResponse("신촌역", "접근성 좋고 맛집이 많아요! 😋", "설명1"),
                        new RecommendedLocationResponse("이대역", "학생들이 많아 맛집이 많아요! 🍜", "설명2")
                )
        );

        final GenerateContentResponse mockGenerateResponse = mock(GenerateContentResponse.class);
        doReturn(mockGenerateResponse)
                .when(googleGeminiClient)
                .generateWith(anyString(), any(GenerateContentConfig.class));

        when(mockGenerateResponse.text()).thenReturn(expectedJsonResponse);

        when(objectMapper.readValue(expectedJsonResponse, RecommendedLocationsResponse.class))
                .thenReturn(expectedResponse);

        // When
        final RecommendedLocationsResponse result = googleGeminiClient.generateResponse(stationNames, candidateNames, requirement);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isNotNull();
            softAssertions.assertThat(result.recommendations()).hasSize(2);
            softAssertions.assertThat(result.recommendations().get(0).locationName()).isEqualTo("신촌역");
            softAssertions.assertThat(result.recommendations().get(0).reason()).isEqualTo("접근성 좋고 맛집이 많아요! 😋");
            softAssertions.assertThat(result.recommendations().get(1).locationName()).isEqualTo("이대역");
            softAssertions.assertThat(result.recommendations().get(1).reason()).isEqualTo("학생들이 많아 맛집이 많아요! 🍜");
        });
    }

    @DisplayName("generateWith() 메서드가 성공적으로 GenerateContentResponse를 반환한다")
    @Test
    void generateWithStringPromptSuccessTest() {
        // Given
        final String prompt = "테스트 프롬프트";
        final GenerateContentConfig config = getBasicConfig();

        final GenerateContentResponse expectedResponse = mock(GenerateContentResponse.class);

        doReturn(expectedResponse).when(googleGeminiClient).generate(any(List.class), any(GenerateContentConfig.class));

        // When
        final GenerateContentResponse result = googleGeminiClient.generateWith(prompt, config);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(result).isNotNull();
            softAssertions.assertThat(result).isEqualTo(expectedResponse);
        });
    }

    @DisplayName("Gemini API 요청 본문이 유효하지 않을 경우 ExternalApiException(INVALID_GEMINI_API_RESPONSE)을 던진다")
    @Test
    void invalidArgumentTest() {
        // Given
        final List<Content> contents = new ArrayList<>();
        final GenerateContentConfig config = getBasicConfig();

        doThrow(new ClientException(400, "INVALID_ARGUMENT", "요청 본문의 형식이 잘못되었습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE);
    }

    @DisplayName("API KEY가 권한이 없을 경우 ExternalApiException(INVALID_GEMINI_API_KEY)을 던진다")
    @Test
    void invalidApiKeyTest() {
        // Given
        final List<Content> contents = getBasicContents();
        final GenerateContentConfig config = getBasicConfig();

        doThrow(new ClientException(403, "PERMISSION_DENIED", "API key not valid. Please pass a valid API key."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_API_KEY);
    }

    @DisplayName("토큰 사용량이 초과된 경우 ExternalApiException(EXCEEDED_GEMINI_API_TOKEN_QUOTA)을 던진다")
    @Test
    void exceededTokenQuotaTest() {
        // Given
        final List<Content> contents = getBasicContents();
        final GenerateContentConfig config = getBasicConfig();

        doThrow(new ClientException(429, "RESOURCE_EXHAUSTED", "비율 제한을 초과했습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.EXCEEDED_GEMINI_API_TOKEN_QUOTA);
    }

    @DisplayName("Gemini API 서버가 일시적으로 응답하지 않을 경우 ExternalApiException(GEMINI_API_SERVER_UNAVAILABLE)을 던진다")
    @Test
    void serverUnavailableTest() {
        // Given
        final List<Content> contents = getBasicContents();
        final GenerateContentConfig config = getBasicConfig();

        doThrow(new ServerException(503, "현재 구매할 수 없음", "서비스가 일시적으로 과부하되거나 다운되었을 수 있습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE);
    }

    @DisplayName("Gemini API 응답이 유효하지 않은 JSON이면 RetryableApiException을 던진다")
    @Test
    void extractResponseInvalidJsonTest() throws JsonProcessingException {
        // Given
        final List<String> stationNames = List.of("강남역", "홍대입구역");
        final List<String> candidateNames = List.of("이태원역", "신사역");
        final List<String> requirement = List.of("맛집이 많은 곳");

        final String invalidJson = "유효하지 않은 JSON";

        final GenerateContentResponse mockGenerateResponse = mock(GenerateContentResponse.class);
        doReturn(mockGenerateResponse)
                .when(googleGeminiClient)
                .generateWith(anyString(), any(GenerateContentConfig.class));

        when(mockGenerateResponse.text()).thenReturn(invalidJson);
        when(objectMapper.readValue(invalidJson, RecommendedLocationsResponse.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateResponse(stationNames, candidateNames, requirement))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
    }
    @DisplayName("Gemini API 응답이 비어있으면 RetryableApiException을 던진다")
    @Test
    void extractResponseEmptyResponseTest() {
        // Given
        final List<String> stationNames = List.of("강남역", "홍대입구역");
        final List<String> candidateNames = List.of("이태원역", "신사역");
        final List<String> requirement = List.of("맛집이 많은 곳");

        final String emptyResponse = "   ";

        final GenerateContentResponse mockGenerateResponse = mock(GenerateContentResponse.class);
        doReturn(mockGenerateResponse)
                .when(googleGeminiClient)
                .generateWith(anyString(), any(GenerateContentConfig.class));

        when(mockGenerateResponse.text()).thenReturn(emptyResponse);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateResponse(stationNames, candidateNames, requirement))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
    }

    @DisplayName("기타 Gemini API 예외 발생 시 ExternalApiException(INVALID_GEMINI_API_RESPONSE)을 던진다")
    @Test
    void otherGeminiApiExceptionTest() {
        // Given
        final List<Content> contents = getBasicContents();
        final GenerateContentConfig config = getBasicConfig();

        doThrow(new ServerException(500, "INTERNAL", "Google 측에서 예기치 않은 오류가 발생했습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE);
    }

    private List<Content> getBasicContents() {
        final List<Content> contents = new ArrayList<>();
        contents.add(Content.fromParts(Part.fromText(PromptGenerator.ADDITIONAL_PROMPT)));
        return contents;
    }

    private GenerateContentConfig getBasicConfig() {
        return GenerateContentConfig.builder()
                .temperature(0.0F)
                .maxOutputTokens(5000)
                .build();
    }

}
