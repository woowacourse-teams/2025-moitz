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
import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.errors.ClientException;
import com.google.genai.errors.ServerException;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

import java.io.IOException;
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
        List<String> stationNames = List.of("강남역", "홍대입구역");
        String requirement = "맛집이 많은 곳";

        String expectedJsonResponse = """
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

        RecommendedLocationResponse expectedResponse = new RecommendedLocationResponse(
                List.of(
                        new LocationNameAndReason("신촌역", "접근성 좋고 맛집이 많아요! 😋"),
                        new LocationNameAndReason("이대역", "학생들이 많아 맛집이 많아요! 🍜")
                )
        );

        GenerateContentResponse mockGenerateResponse = mock(GenerateContentResponse.class);
        doReturn(mockGenerateResponse)
                .when(googleGeminiClient)
                .generateWith(anyString(), any(GenerateContentConfig.class));

        when(mockGenerateResponse.text()).thenReturn(expectedJsonResponse);

        when(objectMapper.readValue(expectedJsonResponse, RecommendedLocationResponse.class))
                .thenReturn(expectedResponse);

        // When
        RecommendedLocationResponse result = googleGeminiClient.generateResponse(stationNames, requirement);

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
        String prompt = "테스트 프롬프트";
        GenerateContentConfig config = getBasicConfig();

        GenerateContentResponse expectedResponse = mock(GenerateContentResponse.class);

        doReturn(expectedResponse).when(googleGeminiClient).generate(any(List.class), any(GenerateContentConfig.class));

        // When
        GenerateContentResponse result = googleGeminiClient.generateWith(prompt, config);

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
        List<Content> contents = new ArrayList<>();
        GenerateContentConfig config = getBasicConfig();

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
        List<Content> contents = getBasicContents();
        GenerateContentConfig config = getBasicConfig();

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
        List<Content> contents = getBasicContents();
        GenerateContentConfig config = getBasicConfig();

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
        List<Content> contents = getBasicContents();
        GenerateContentConfig config = getBasicConfig();

        doThrow(new ServerException(503, "현재 구매할 수 없음", "서비스가 일시적으로 과부하되거나 다운되었을 수 있습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.GEMINI_API_SERVER_UNAVAILABLE);
    }

    @DisplayName("Gemini API 응답이 유효하지 않은 JSON이면 RetryableApiException을 던진다")
    @Test
    void extractResponseInvalidJsonTest() throws IOException {
        // Given
        String invalidJson = "유효하지 않은 JSON";
        GenerateContentResponse fakeResponse = GenerateContentResponse.builder()
                .candidates(List.of(
                        Candidate.builder()
                                .content(Content.fromParts(Part.fromText(invalidJson)))
                                .build()
                ))
                .build();

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.extractResponse(fakeResponse))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
    }

    @DisplayName("Gemini API 응답이 비어있으면 RetryableApiException을 던진다")
    @Test
    void extractResponseEmptyResponseTest() {
        // Given
        String emptyResponse = "   ";
        GenerateContentResponse fakeResponse = GenerateContentResponse.builder()
                .candidates(List.of(
                        Candidate.builder()
                                .content(Content.fromParts(Part.fromText(emptyResponse)))
                                .build()
                ))
                .build();

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.extractResponse(fakeResponse))
                .isInstanceOf(RetryableApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_RESPONSE_FORMAT);
    }

    @DisplayName("기타 Gemini API 예외 발생 시 ExternalApiException(INVALID_GEMINI_API_RESPONSE)을 던진다")
    @Test
    void otherGeminiApiExceptionTest() {
        // Given
        List<Content> contents = getBasicContents();
        GenerateContentConfig config = getBasicConfig();

        doThrow(new ServerException(500, "INTERNAL", "Google 측에서 예기치 않은 오류가 발생했습니다."))
                .when(googleGeminiClient)
                .generate(contents, config);

        // When & Then
        assertThatThrownBy(() -> googleGeminiClient.generateWith(contents, config))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE);
    }

    private List<Content> getBasicContents() {
        List<Content> contents = new ArrayList<>();
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
