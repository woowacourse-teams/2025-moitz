package com.f12.moitz.infrastructure.client.perplexity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

class PerplexityClientTest {

    private MockWebServer mockWebServer;
    private PerplexityClient perplexityClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        perplexityClient = new PerplexityClient(webClient, objectMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Perplexity API 호출에 성공하고, 추천 장소 응답을 올바르게 파싱한다")
    void generateResponseSuccess() throws JsonProcessingException {
        // Given
        var recommendedLocationResponse = new RecommendedLocationResponse(
                List.of(new LocationNameAndReason("강남역", "맛집 많음! 😋"))
        );
        final String content = objectMapper.writeValueAsString(recommendedLocationResponse);

        final String escapedContent = content.replace("\"", "\\\"");
        final String mockResponseJson = String.format("{ \"choices\": [ { \"message\": { \"content\": \"%s\" } } ] }", escapedContent);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When
        final RecommendedLocationResponse actualResponse = perplexityClient.generateResponse(List.of("이수역"), "맛집");

        // Then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.recommendations()).hasSize(1);
        assertThat(actualResponse.recommendations().getFirst().locationName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("API 응답이 비정상적인 JSON 포맷일 경우, INVALID_PERPLEXITY_API_RESPONSE 예외를 던진다")
    void invalidJsonResponse() {
        // Given
        final String malformedContent = "{\"recommendations\":[{\"locationName\":\"강남역\" \"reason\":\"맛집 많음\"}]}";
        final String escapedContent = malformedContent.replace("\"", "\\\"");
        final String mockResponseJson = String.format("{ \"choices\": [ { \"message\": { \"content\": \"%s\" } } ] }", escapedContent);

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> perplexityClient.generateResponse(List.of("이수역"), "맛집"))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_PERPLEXITY_API_RESPONSE);
    }

    @Test
    @DisplayName("401 Unauthorized 에러 발생 시, INVALID_PERPLEXITY_API_KEY 예외를 던진다")
    void handle401Unauthorized() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        // When & Then
        assertThatThrownBy(() -> perplexityClient.generateResponse(List.of("이수역"), "맛집"))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.INVALID_PERPLEXITY_API_KEY);
    }

    @Test
    @DisplayName("402 Payment Required 에러 발생 시, EXCEEDED_PERPLEXITY_API_TOKEN_QUOTA 예외를 던진다")
    void handle402PaymentRequired() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(402));

        // When & Then
        assertThatThrownBy(() -> perplexityClient.generateResponse(List.of("이수역"), "맛집"))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.EXCEEDED_PERPLEXITY_API_TOKEN_QUOTA);
    }

    @Test
    @DisplayName("429 Too Many Requests 에러 발생 시, 재시도 후 PERPLEXITY_API_SERVER_UNRESPONSIVE 예외를 던진다")
    void handle429TooManyRequests() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(429)); // First attempt
        mockWebServer.enqueue(new MockResponse().setResponseCode(429)); // Retry attempt

        // When & Then
        assertThatThrownBy(() -> perplexityClient.generateResponse(List.of("이수역"), "맛집"))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNRESPONSIVE);
    }

    @Test
    @DisplayName("5xx 서버 에러 발생 시, 재시도 후 PERPLEXITY_API_SERVER_UNRESPONSIVE 예외를 던진다")
    void handle5xxServerError() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When & Then
        assertThatThrownBy(() -> perplexityClient.generateResponse(List.of("이수역"), "맛집"))
                .isInstanceOf(ExternalApiException.class)
                .extracting("errorCode")
                .isEqualTo(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNRESPONSIVE);
    }

}
