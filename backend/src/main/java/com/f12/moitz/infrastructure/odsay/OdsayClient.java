package com.f12.moitz.infrastructure.odsay;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.odsay.dto.OdsayErrorResponse;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class OdsayClient {

    private static final String ODSAY_API_URL = "https://api.odsay.com/v1/api";
    private static final int SEARCH_PATH_TYPE = 1;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${odsay.api.key}")
    private String odsayApiKey;

    public SubwayRouteSearchResponse getRoute(final Point startPoint, final Point endPoint) {
        // TODO: 예외 처리, 응답에 따른 핸들링
        String urlInfo = ODSAY_API_URL + "/searchPubTransPathT?SX=%f&SY=%f&EX=%f&EY=%f&apiKey=%s&searchPathType=%d";

        final String url = String.format(
                urlInfo,
                startPoint.getX(),
                startPoint.getY(),
                endPoint.getX(),
                endPoint.getY(),
                odsayApiKey,
                SEARCH_PATH_TYPE
        );

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            log.error("인터럽트 발생", e);
            Thread.currentThread().interrupt();
        }

        final SubwayRouteSearchResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handleError(res)
                )
                .body(SubwayRouteSearchResponse.class);
        log.info("Odsay 응답 성공, url : {}", url);
        return response;
    }

    private void handleError(ClientHttpResponse res) {
        try {
            byte[] body = res.getBody().readAllBytes();
            OdsayErrorResponse error = objectMapper.readValue(body,
                    OdsayErrorResponse.class);
            log.error(error.msg());
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
        } catch (IOException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
        }
    }

}
