package com.f12.moitz.infrastructure.client.open;

import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private static final String OPEN_BASE_SEARCH = "/getShtrmPath";
    private static final String SEARCH_DATE_HOUR = "2025-08-11 18:00:00";
    private static final String SEARCH_TYPE_DURATION = "duration";
    private static final String SEARCH_TYPE_TRANSFER = "transfer";

    private final RestClient openRestClient;
    private final ObjectMapper objectMapper;

    @Value("${open.api.key}")
    private String openApiKey;


    public SubwayRouteResponse searchShortestTimeRoute(final String startPlaceName, final String endPlaceName) {
        return searchRoute(startPlaceName, endPlaceName, SEARCH_TYPE_DURATION);
    }

    public SubwayRouteResponse searchMinimumTransferRoute(final String startPlaceName, final String endPlaceName) {
        return searchRoute(startPlaceName, endPlaceName, SEARCH_TYPE_TRANSFER);
    }

    public SubwayRouteResponse searchRoute(
            final String startPlaceName,
            final String endPlaceName,
            final String searchType
    ) {
        try {
            return openRestClient.get()
                    .uri(uriBuilder -> {
                                var uri = uriBuilder
                                        .path(OPEN_BASE_SEARCH)
                                        .queryParam("dataType", "JSON")
                                        .queryParam("dptreStnNm", getStationName(startPlaceName))
                                        .queryParam("arvlStnNm", getStationName(endPlaceName))
                                        .queryParam("searchDt", SEARCH_DATE_HOUR)
                                        .queryParam("searchType", searchType)
                                        .build();

                                // API 키가 이미 URL 인코딩되어 있으므로 수동으로 추가
                                String finalUri = uri.toString() + "&serviceKey=" + openApiKey;

                                return URI.create(finalUri);
                            }
                    )
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            (req, res) -> {
                                throw new RuntimeException("공공 API 응답의 상태코드가 400 혹은 500입니다.");
                            }
                    )
                    .body(SubwayRouteResponse.class);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("공공 API 응답이 제대로 생성되지 않았습니다.");
        }
    }

    private String getStationName(final String stationName) {
        if (!stationName.equals("서울역") && stationName.endsWith("역")) {
            return stationName.substring(0, stationName.length() - 1);
        }
        return stationName;
    }

}
