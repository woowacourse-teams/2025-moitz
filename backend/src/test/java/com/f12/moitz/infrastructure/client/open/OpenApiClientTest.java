package com.f12.moitz.infrastructure.client.open;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

class OpenApiClientTest {

    private MockWebServer mockWebServer;
    private OpenApiClient openApiClient;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        final String baseUrl = mockWebServer.url("/").toString();
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        openApiClient = new OpenApiClient(restClient);
        ReflectionTestUtils.setField(openApiClient, "openApiKey", "test-api-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("최단 시간 경로 조회에 성공한다")
    void searchShortestTimeRoute() {
        // Given
        String mockResponseJson = """
                {
                    "header": {
                        "resultCode": "200",
                        "resultMsg": "성공"
                    },
                    "body": {
                        "searchType": "duration",
                        "totalDstc": 15074,
                        "totalreqHr": 1770,
                        "totalCardCrg": 0,
                        "trsitNmtm": 1,
                        "trfstnNms": [
                            {
                                "stnNm": "사당",
                                "dptreLineNm": "2호선",
                                "arvlLineNm": "4호선"
                            }
                        ],
                        "exclTrfstnNms": [],
                        "thrghStnNms": [],
                        "schInclYn": "Y",
                        "paths": [
                            {
                                "dptreStn": {
                                    "stnCd": "0222",
                                    "stnNo": "222",
                                    "stnNm": "강남",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                    "arvlStn": {
                                    "stnCd": "0223",
                                    "stnNo": "223",
                                    "stnNm": "교대",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 1200,
                                "reqHr": 90,
                                "wtngHr": 0,
                                "tmnlStnNm": "성수",
                                "tmnlStnCd": "0211",
                                "upbdnbSe": "내선",
                                "trainno": "2348",
                                "trainDptreTm": "18:00:00",
                                "trainArvlTm": "18:01:30",
                                "trsitYn": "N",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            },
                            {
                                "dptreStn": {
                                    "stnCd": "0226",
                                    "stnNo": "226",
                                    "stnNm": "사당",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                "arvlStn": {
                                    "stnCd": "0433",
                                    "stnNo": "433",
                                    "stnNm": "사당",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 74,
                                "reqHr": 115,
                                "wtngHr": 155,
                                "tmnlStnNm": null,
                                "tmnlStnCd": null,
                                "upbdnbSe": null,
                                "trainno": null,
                                "trainDptreTm": null,
                                "trainArvlTm": null,
                                "trsitYn": "Y",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            },
                            {
                                "dptreStn": {
                                    "stnCd": "0427",
                                    "stnNo": "427",
                                    "stnNm": "숙대입구",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "arvlStn": {
                                    "stnCd": "0426",
                                    "stnNo": "426",
                                    "stnNm": "서울역",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 1000,
                                "reqHr": 90,
                                "wtngHr": 0,
                                "tmnlStnNm": "불암산",
                                "tmnlStnCd": "0409",
                                "upbdnbSe": "상행",
                                "trainno": "4160",
                                "trainDptreTm": "18:28:00",
                                "trainArvlTm": "18:29:30",
                                "trsitYn": "N",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            }
                        ]
                    }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When
        SubwayRouteResponse response = openApiClient.searchShortestTimeRoute("강남역", "서울역");

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).isNotNull();
            softAssertions.assertThat(response.header().resultCode()).isEqualTo("200");
            softAssertions.assertThat(response.body().paths()).hasSize(3);
            softAssertions.assertThat(response.body().paths().getFirst().dptreStn().stnNm()).isEqualTo("강남");
            softAssertions.assertThat(response.body().paths().getLast().arvlStn().stnNm()).isEqualTo("서울역");
        });
    }

    @Test
    @DisplayName("최소 환승 경로 조회에 성공한다")
    void searchMinimumTransferRoute() {
        // Given
        String mockResponseJson = """
                {
                    "header": {
                        "resultCode": "200",
                        "resultMsg": "성공"
                    },
                    "body": {
                        "searchType": "duration",
                        "totalDstc": 15074,
                        "totalreqHr": 1770,
                        "totalCardCrg": 0,
                        "trsitNmtm": 1,
                        "trfstnNms": [
                            {
                                "stnNm": "사당",
                                "dptreLineNm": "2호선",
                                "arvlLineNm": "4호선"
                            }
                        ],
                        "exclTrfstnNms": [],
                        "thrghStnNms": [],
                        "schInclYn": "Y",
                        "paths": [
                            {
                                "dptreStn": {
                                    "stnCd": "0222",
                                    "stnNo": "222",
                                    "stnNm": "강남",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                    "arvlStn": {
                                    "stnCd": "0223",
                                    "stnNo": "223",
                                    "stnNm": "교대",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 1200,
                                "reqHr": 90,
                                "wtngHr": 0,
                                "tmnlStnNm": "성수",
                                "tmnlStnCd": "0211",
                                "upbdnbSe": "내선",
                                "trainno": "2348",
                                "trainDptreTm": "18:00:00",
                                "trainArvlTm": "18:01:30",
                                "trsitYn": "N",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            },
                            {
                                "dptreStn": {
                                    "stnCd": "0226",
                                    "stnNo": "226",
                                    "stnNm": "사당",
                                    "lineNm": "2호선",
                                    "brlnNm": null
                                },
                                "arvlStn": {
                                    "stnCd": "0433",
                                    "stnNo": "433",
                                    "stnNm": "사당",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 74,
                                "reqHr": 115,
                                "wtngHr": 155,
                                "tmnlStnNm": null,
                                "tmnlStnCd": null,
                                "upbdnbSe": null,
                                "trainno": null,
                                "trainDptreTm": null,
                                "trainArvlTm": null,
                                "trsitYn": "Y",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            },
                            {
                                "dptreStn": {
                                    "stnCd": "0427",
                                    "stnNo": "427",
                                    "stnNm": "숙대입구",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "arvlStn": {
                                    "stnCd": "0426",
                                    "stnNo": "426",
                                    "stnNm": "서울역",
                                    "lineNm": "4호선",
                                    "brlnNm": null
                                },
                                "stnSctnDstc": 1000,
                                "reqHr": 90,
                                "wtngHr": 0,
                                "tmnlStnNm": "불암산",
                                "tmnlStnCd": "0409",
                                "upbdnbSe": "상행",
                                "trainno": "4160",
                                "trainDptreTm": "18:28:00",
                                "trainArvlTm": "18:29:30",
                                "trsitYn": "N",
                                "etrnYn": "N",
                                "nonstopYn": "N"
                            }
                        ]
                    }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When
        SubwayRouteResponse response = openApiClient.searchMinimumTransferRoute("강남역", "서울역");

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response).isNotNull();
            softAssertions.assertThat(response.header().resultCode()).isEqualTo("200");
            softAssertions.assertThat(response.body().trsitNmtm()).isEqualTo(1);
            softAssertions.assertThat(response.body().trfstnNms()).hasSize(1);
        });
    }

    @Test
    @DisplayName("HTTP 4xx/5xx 에러 시 INVALID_OPEN_API_RESPONSE 예외를 던진다")
    void throwsExceptionOn4xxOr5xxErrors() {
        // Given
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader("Content-Type", "application/json")
                        .setBody("Internal Server Error")
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
    }

    @Test
    @DisplayName("잘못된 API 키(returnReasonCode: 30) 시 INVALID_OPEN_API_KEY 예외를 던진다")
    void throwsInvalidApiKeyException() {
        // Given
        String xmlErrorResponse = """
                <OpenAPI_ServiceResponse>
                  <cmmMsgHeader>
                    <errMsg>SERVICE ERROR</errMsg>
                    <returnAuthMsg>SERVICE_KEY_IS_NOT_REGISTERED_ERROR</returnAuthMsg>
                    <returnReasonCode>30</returnReasonCode>
                  </cmmMsgHeader>
                </OpenAPI_ServiceResponse>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/xml")
                        .setBody(xmlErrorResponse)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_OPEN_API_KEY);
    }

    @Test
    @DisplayName("토큰 쿼터 초과(returnReasonCode: 22) 시 EXCEEDED_OPEN_API_TOKEN_QUOTA 예외를 던진다")
    void throwsExceededTokenQuotaException() {
        // Given
        String xmlErrorResponse = """
                <OpenAPI_ServiceResponse>
                  <cmmMsgHeader>
                    <errMsg>SERVICE ERROR</errMsg>
                    <returnAuthMsg>LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR</returnAuthMsg>
                    <returnReasonCode>22</returnReasonCode>
                  </cmmMsgHeader>
                </OpenAPI_ServiceResponse>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/xml")
                        .setBody(xmlErrorResponse)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.EXCEEDED_OPEN_API_TOKEN_QUOTA);
    }

    @Test
    @DisplayName("서버 이용 불가(returnReasonCode: 23) 시 OPEN_API_SERVER_UNAVAILABLE 예외를 던진다")
    void throwsServerUnavailableException() {
        // Given
        String xmlErrorResponse = """
                <OpenAPI_ServiceResponse>
                  <cmmMsgHeader>
                    <errMsg>SERVICE ERROR</errMsg>
                    <returnAuthMsg>LIMITED_NUMBER_OF_SERVICE_REQUESTS_PER_SECOND_EXCEEDS_ERROR</returnAuthMsg>
                    <returnReasonCode>23</returnReasonCode>
                  </cmmMsgHeader>
                </OpenAPI_ServiceResponse>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/xml")
                        .setBody(xmlErrorResponse)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(RetryableApiException.class)
                .extracting(e -> ((RetryableApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.OPEN_API_SERVER_UNAVAILABLE);
    }

    @Test
    @DisplayName("서버 무응답(returnReasonCode: 12, 20, 31, 32) 시 OPEN_API_SERVER_UNRESPONSIVE 예외를 던진다")
    void throwsServerUnresponsiveException() {
        // Given
        String xmlErrorResponse = """
                <OpenAPI_ServiceResponse>
                  <cmmMsgHeader>
                    <errMsg>SERVICE ERROR</errMsg>
                    <returnAuthMsg>NO_OPENAPI_SERVICE_ERROR</returnAuthMsg>
                    <returnReasonCode>12</returnReasonCode>
                  </cmmMsgHeader>
                </OpenAPI_ServiceResponse>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/xml")
                        .setBody(xmlErrorResponse)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.OPEN_API_SERVER_UNRESPONSIVE);
    }

    @Test
    @DisplayName("기타 에러 코드 시 INVALID_OPEN_API_RESPONSE 예외를 던진다")
    void throwsInvalidResponseExceptionForUnknownErrorCode() {
        // Given
        String xmlErrorResponse = """
                <OpenAPI_ServiceResponse>
                  <cmmMsgHeader>
                    <errMsg>SERVICE ERROR</errMsg>
                    <returnAuthMsg>APPLICATION ERROR</returnAuthMsg>
                    <returnReasonCode>1</returnReasonCode>
                  </cmmMsgHeader>
                </OpenAPI_ServiceResponse>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/xml")
                        .setBody(xmlErrorResponse)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchShortestTimeRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
    }

    @Test
    @DisplayName("응답이 null인 경우 INVALID_OPEN_API_RESPONSE 예외를 던진다")
    void throwsExceptionWhenResponseIsNull() {
        // Given
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody("null")
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
    }

    @Test
    @DisplayName("응답의 resultCode가 99인 경우 OPEN_API_SERVER_UNAVAILABLE 예외를 던진다")
    void throwsServerUnavailableExceptionWhenResultCodeIs99() {
        // Given
        String mockResponseJson = """
                {
                    "header": {
                        "resultCode": "99",
                        "resultMsg": "서버 내부 오류입니다."
                    }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchShortestTimeRoute("강남역", "서울역"))
                .isInstanceOf(RetryableApiException.class)
                .extracting(e -> ((RetryableApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.OPEN_API_SERVER_UNAVAILABLE);
    }

    @Test
    @DisplayName("응답의 resultCode가 99, 200, 00이 아닌 경우 INVALID_OPEN_API_RESPONSE 예외를 던진다")
    void throwsInvalidResponseExceptionWhenResultCodeIsInvalid() {
        // Given
        String mockResponseJson = """
                {
                    "header": {
                        "resultCode": "01",
                        "resultMsg": "출발역 또는 도착역이 입력되지 않았습니다."
                    }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(mockResponseJson)
        );

        // When & Then
        assertThatThrownBy(() -> openApiClient.searchMinimumTransferRoute("강남역", "서울역"))
                .isInstanceOf(ExternalApiException.class)
                .extracting(e -> ((ExternalApiException) e).getErrorCode())
                .isEqualTo(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
    }

}
