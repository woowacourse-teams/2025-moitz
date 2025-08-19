package com.f12.moitz.infrastructure.client.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class KakaoMapClientTest {

    private MockWebServer mockWebServer;
    private KakaoMapClient kakaoMapClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        final String baseUrl = mockWebServer.url("/").toString();
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        kakaoMapClient = new KakaoMapClient(restClient, objectMapper);
        ReflectionTestUtils.setField(kakaoMapClient, "kakaoApiKey", "test-api-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("장소 이름으로 좌표 조회에 성공한다")
    void searchPointBy() throws InterruptedException {
        // Given
        String mockResponseJson = """
                {
                "documents": [{
                "category_group_name" : "지하철역",
                "x": "127.027618",
                "y": "37.497949"
                }]
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When
        Point actualPoint = kakaoMapClient.searchPointBy("강남역");

        // Then
        assertThat(actualPoint.getX()).isEqualTo(127.027618);
        assertThat(actualPoint.getY()).isEqualTo(37.497949);
    }

    @Test
    @DisplayName("키워드로 장소 목록 조회에 성공한다")
    void searchPlacesBy() {
        // Given
        String mockResponseJson = """
                { "documents": [{"place_name": "스타벅스 강남R점"}] }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        SearchPlacesRequest request = new SearchPlacesRequest("카페", 127.0, 37.0, 1000);

        // When
        var actualResponse = kakaoMapClient.searchPlacesBy(request);

        // Then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.documents()).hasSize(1);
        assertThat(actualResponse.toString()).contains("스타벅스 강남R점");
    }


    @Test
    @DisplayName("카카오맵 API가 재시도 가능한 에러(-1)를 반환하면 TEMPORARILY_INVALID 예외를 던진다")
    void throwsRetryableException() {
        // Given
        String errorResponseJson = """
                { "code": "-1", "msg": "Temporary Error" }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader("Content-Type", "application/json")
                        .setBody(errorResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> kakaoMapClient.searchPointBy("강남역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.TEMPORARILY_INVALID_KAKAO_MAP_API_RESPONSE);
    }

    @Test
    @DisplayName("카카오맵 API가 쿼터 초과 에러(-10)를 반환하면 EXCEEDED_QUOTA 예외를 던진다")
    void throwsQuotaException() {
        // Given
        String errorResponseJson = """
                { "code": "-10", "msg": "API Usage Limit Exceeded" }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(403)
                        .setHeader("Content-Type", "application/json")
                        .setBody(errorResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> kakaoMapClient.searchPointBy("강남역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.EXCEEDED_KAKAO_MAP_API_TOKEN_QUOTA);
    }

    @Test
    @DisplayName("카카오맵 API가 그 외의 에러를 반환하면 INVALID_RESPONSE 예외를 던진다")
    void throwsInvalidResponseException() {
        // Given
        String errorResponseJson = """
                { "code": "-2", "msg": "Invalid Parameter" }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(400)
                        .setHeader("Content-Type", "application/json")
                        .setBody(errorResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> kakaoMapClient.searchPointBy("강남역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
    }

}

