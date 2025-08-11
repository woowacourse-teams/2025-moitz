package com.f12.moitz.infrastructure.odsay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.odsay.dto.OdsayErrorResponse;
import com.f12.moitz.infrastructure.odsay.dto.ResultResponse;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class OdsayClientTest {

    @InjectMocks
    private OdsayClient odsayClient;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(odsayClient, "odsayApiKey", "test-api-key");
        ReflectionTestUtils.setField(odsayClient, "objectMapper", objectMapper);

        given(restClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri(any(String.class))).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.onStatus(any(), any())).willReturn(responseSpec);
    }

    @DisplayName("API 호출에 성공하고 경로 정보를 반환한다.")
    @Test
    void getRoute_Success() {
        // given
        Point startPoint = new Point(127.0, 37.5);
        Point endPoint = new Point(127.1, 37.6);

        SubwayRouteSearchResponse expectedResponse = new SubwayRouteSearchResponse(
                new ResultResponse(1, Collections.emptyList()),
                null
        );

        given(responseSpec.body(SubwayRouteSearchResponse.class)).willReturn(expectedResponse);

        // when
        SubwayRouteSearchResponse actualResponse = odsayClient.getRoute(startPoint, endPoint);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("API 응답에 429 에러가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void getRoute_ApiBlockedError() {
        // given
        Point startPoint = new Point(127.0, 37.5);
        Point endPoint = new Point(127.1, 37.6);

        OdsayErrorResponse error = new OdsayErrorResponse("429", "API rate limit exceeded", "id");
        SubwayRouteSearchResponse errorResponse = new SubwayRouteSearchResponse(
                null,
                error
        );

        given(responseSpec.body(SubwayRouteSearchResponse.class)).willReturn(errorResponse);

        // when & then
        assertThatThrownBy(() -> odsayClient.getRoute(startPoint, endPoint))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage(ExternalApiErrorCode.ODSAY_API_BLOCKED.getMessage());
    }

    @DisplayName("API 응답에 일반 에러가 포함되어 있으면 예외를 발생시킨다.")
    @Test
    void getRoute_InvalidApiResponse_GeneralError() {
        // given
        Point startPoint = new Point(127.0, 37.5);
        Point endPoint = new Point(127.1, 37.6);

        OdsayErrorResponse error = new OdsayErrorResponse("500", "Internal Server Error", "id");
        SubwayRouteSearchResponse errorResponse = new SubwayRouteSearchResponse(
                null,
                error
        );

        given(responseSpec.body(SubwayRouteSearchResponse.class)).willReturn(errorResponse);

        // when & then
        assertThatThrownBy(() -> odsayClient.getRoute(startPoint, endPoint))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE.getMessage());
    }

    @DisplayName("API 응답의 result가 null이면 예외를 발생시킨다.")
    @Test
    void getRoute_InvalidApiResponse_NullResult() {
        // given
        Point startPoint = new Point(127.0, 37.5);
        Point endPoint = new Point(127.1, 37.6);

        SubwayRouteSearchResponse responseWithNullResult = new SubwayRouteSearchResponse(null, null);

        given(responseSpec.body(SubwayRouteSearchResponse.class)).willReturn(responseWithNullResult);

        // when & then
        assertThatThrownBy(() -> odsayClient.getRoute(startPoint, endPoint))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE.getMessage());
    }

    @DisplayName("API 응답이 null이면 예외를 발생시킨다.")
    @Test
    void getRoute_InvalidApiResponse_NullResponse() {
        // given
        Point startPoint = new Point(127.0, 37.5);
        Point endPoint = new Point(127.1, 37.6);

        given(responseSpec.body(SubwayRouteSearchResponse.class)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> odsayClient.getRoute(startPoint, endPoint))
                .isInstanceOf(ExternalApiException.class)
                .hasMessage(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE.getMessage());
    }
}
