package com.f12.moitz.infrastructure.client.kakao;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoMapErrorResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoMapClient {

    private static final String SEARCH_PLACE_URL = "/keyword.json?query=%s&x=%s&y=%s&radius=%d";
    private static final String SEARCH_POINT_URL = "/keyword.json?query=%s";
    private static final List<String> ERROR_CODE_CAN_RETRY = List.of("-1", "-7", "-603");

    private final RestClient kakaoRestClient;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public Point searchPointBy(final String placeName) {
        final String url = String.format(SEARCH_POINT_URL, placeName);
        final KakaoApiResponse response = getData(url);
        return new Point(response.findStationX(), response.findStationY());
    }

    public KakaoApiResponse searchPlacesBy(final SearchPlacesRequest request) {
        return searchPlacesBy(
                request.query(),
                String.valueOf(request.longitude()),
                String.valueOf(request.latitude()),
                request.radius()
        );
    }

    private KakaoApiResponse searchPlacesBy(
            final String keyword,
            final String longitude,
            final String latitude,
            final int radius
    ) {
        final String url = String.format(
                SEARCH_PLACE_URL,
                keyword,
                longitude,
                latitude,
                radius
        );
        return getData(url);
    }

    private KakaoApiResponse getData(final String url) {
        final KakaoApiResponse response = kakaoRestClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handleError(res)
                )
                .body(KakaoApiResponse.class);

        return response;
    }

    private void handleError(ClientHttpResponse res) {
        try {
            byte[] body = res.getBody().readAllBytes();
            KakaoMapErrorResponse error = objectMapper.readValue(body,
                    KakaoMapErrorResponse.class);
            log.error(error.msg());
            if (ERROR_CODE_CAN_RETRY.contains(error.code())) {
                throw new ExternalApiException(ExternalApiErrorCode.TEMPORARILY_INVALID_KAKAO_MAP_API_RESPONSE);
            }
            if ("-10".equals(error.code())) {
                throw new ExternalApiException(ExternalApiErrorCode.EXCEEDED_KAKAO_MAP_API_TOKEN_QUOTA);
            }
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
        } catch (IOException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
        }
    }

}
