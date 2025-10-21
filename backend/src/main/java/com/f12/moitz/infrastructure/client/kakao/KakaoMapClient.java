package com.f12.moitz.infrastructure.client.kakao;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoImageApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoMapErrorResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchImageRequest;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoMapClient {

    private static final String SEARCH_PLACE_URL = "/local/search/keyword.json?query=%s&x=%s&y=%s&radius=%d";
    private static final String SEARCH_PLACE_WITH_SIZE_URL = "/local/search/keyword.json?query=%s&x=%s&y=%s&radius=%d&size=%d";
    private static final String SEARCH_POINT_URL = "/local/search/keyword.json?query=%s";
    private static final String SEARCH_IMAGE_URL = "/search/image?query=%s&page=%d&size=%d";
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

    public KakaoApiResponse searchPlacesBy(final SearchPlacesLimitQuantityRequest request) {
        return searchPlacesBy(
                request.query(),
                request.name(),
                String.valueOf(request.longitude()),
                String.valueOf(request.latitude()),
                request.radius(),
                request.size()
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

    private KakaoApiResponse searchPlacesBy(
            final String keyword,
            final String stationName,
            final String longitude,
            final String latitude,
            final int radius,
            final int size
    ) {
        final String url = String.format(
                SEARCH_PLACE_WITH_SIZE_URL,
                keyword,
                longitude,
                latitude,
                radius,
                size
        );
        final KakaoApiResponse response = getData(url);
        return enrichWithImages(response, stationName);
    }

    public KakaoImageApiResponse searchImagesBy(final SearchImageRequest request) {
        final String url = String.format(
                SEARCH_IMAGE_URL,
                request.getQuery(),
                request.getPage(),
                request.getSize()
        );
        return getImageData(url);
    }

    private KakaoApiResponse getData(final String url) {
        return kakaoRestClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handleError(res)
                )
                .body(KakaoApiResponse.class);
    }

    private KakaoImageApiResponse getImageData(final String url) {
        return kakaoRestClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handleError(res)
                )
                .body(KakaoImageApiResponse.class);
    }

    private KakaoApiResponse enrichWithImages(final KakaoApiResponse response, final String stationName) {
        final List<DocumentResponse> documentsWithImages = response.documents().stream()
                .map(documentResponse -> addImageUrlToDocument(documentResponse, stationName))
                .collect(Collectors.toList());
        log.debug(documentsWithImages.toString());
        return response.withImageUrls(documentsWithImages);
    }

    private DocumentResponse addImageUrlToDocument(final DocumentResponse document, final String stationName) {
        try {
            final SearchImageRequest imageRequest = new SearchImageRequest(stationName, document.placeName(), 1, 1);
            final KakaoImageApiResponse imageResponse = searchImagesBy(imageRequest);

            if (imageResponse.documents() != null && !imageResponse.documents().isEmpty()) {
                final String imageUrl = imageResponse.documents().get(0).imageUrl();
                return document.withImageUrl(imageUrl);
            }
            log.info("No image found for place: {}", document.placeName());
            return document.withImageUrl(null);
        } catch (ExternalApiException e) {
            log.warn("Failed to fetch image for place: {}", document.placeName(), e);
            return document.withImageUrl(null);
        }
    }

    private void handleError(ClientHttpResponse res) {
        try {
            final byte[] body = res.getBody().readAllBytes();
            final KakaoMapErrorResponse error = objectMapper.readValue(body,
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
