package com.f12.moitz.infrastructure.client.odsay;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.client.odsay.dto.SubwayRouteSearchResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OdsayMultiClient {

    private static final String ODSAY_BASE_SEARCH = "/searchPubTransPathT";
    private static final int SEARCH_PATH_TYPE = 1;

    private final WebClient odsayWebClient;

    @Value("${odsay.api.key}")
    private String odsayApiKey;

    public Mono<SubwayRouteSearchResponse> getRoute(final Point startPoint, final Point endPoint) {
        return Mono.delay(Duration.ofMillis(300))
                .then(
                        odsayWebClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path(ODSAY_BASE_SEARCH)
                                        .queryParam("SX", startPoint.getX())
                                        .queryParam("SY", startPoint.getY())
                                        .queryParam("EX", endPoint.getX())
                                        .queryParam("EY", endPoint.getY())
                                        .queryParam("apiKey", odsayApiKey)
                                        .queryParam("searchPathType", SEARCH_PATH_TYPE)
                                        .build()
                                ).retrieve()
                                .onStatus(
                                        status -> status.is4xxClientError() || status.is5xxServerError(),
                                        clientResponse -> Mono.error(new ExternalApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE))
                                )
                                .bodyToMono(SubwayRouteSearchResponse.class)
                                .doOnNext(response -> {
                                    if (response == null) {
                                        throw new ExternalApiException(ExternalApiErrorCode.ODSAY_API_SERVER_UNRESPONSIVE);
                                    }
                                    if (response.error() != null) {
                                        log.error("코드 : {} / 메시지 : {}", response.error().code(), response.error().message());
                                        throw new ExternalApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
                                    }
                                    if (response.result() == null) {
                                        throw new ExternalApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
                                    }
                                })
                );
    }

}
