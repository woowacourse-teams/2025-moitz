package com.f12.moitz.infrastructure.client.open;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownContentTypeException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiClient {

    private static final String OPEN_BASE_SEARCH = "/getShtrmPath";
    private static final String SEARCH_DATE_HOUR = "2025-08-11 18:00:00";
    private static final String SEARCH_TYPE_DURATION = "duration";
    private static final String SEARCH_TYPE_TRANSFER = "transfer";

    private final RestClient openRestClient;

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
            SubwayRouteResponse response = openRestClient.get()
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
                                throw new ExternalApiException(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
                            }
                    )
                    .body(SubwayRouteResponse.class);

            validateResponse(response);

            return response;
        } catch (UnknownContentTypeException e) {
            final String xml = e.getResponseBodyAsString();
            final Document document = getDocument(xml);

            final String returnAuthMsg = document.getElementsByTagName("returnAuthMsg").item(0).getTextContent();
            log.error("공공 API 에러: {}", returnAuthMsg);

            final String returnReasonCode = document.getElementsByTagName("returnReasonCode").item(0).getTextContent();

            switch (returnReasonCode) {
                case "30" -> throw new ExternalApiException(ExternalApiErrorCode.INVALID_OPEN_API_KEY);
                case "22" -> throw new ExternalApiException(ExternalApiErrorCode.EXCEEDED_OPEN_API_TOKEN_QUOTA);
                case "23" -> throw new RetryableApiException(ExternalApiErrorCode.OPEN_API_SERVER_UNAVAILABLE);
                case "12", "20", "31", "32" ->
                        throw new ExternalApiException(ExternalApiErrorCode.OPEN_API_SERVER_UNRESPONSIVE);
                default -> throw new ExternalApiException(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
            }
        }
    }

    private void validateResponse(final SubwayRouteResponse response) {
        if (response == null) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
        }

        String resultCode = response.header().resultCode();

        if (resultCode.equals("99")) {
            throw new RetryableApiException(ExternalApiErrorCode.OPEN_API_SERVER_UNAVAILABLE);
        }
        if (!resultCode.equals("200") && !resultCode.equals("00")) {
            log.error("공공 API 에러: {}", response.header().resultMsg());
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_OPEN_API_RESPONSE);
        }
    }

    private Document getDocument(final String xml) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(xml))
            );
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStationName(final String stationName) {
        if ("총신대입구(이수)역".equals(stationName)) {
            return "총신대입구";
        }
        if (!"서울역".equals(stationName) && stationName.endsWith("역")) {
            return stationName.substring(0, stationName.length() - 1);
        }
        return stationName;
    }

}
