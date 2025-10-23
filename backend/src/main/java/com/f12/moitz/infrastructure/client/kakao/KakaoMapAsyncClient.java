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
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoMapAsyncClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(KakaoMapConstants.REQUEST_TIMEOUT_SECONDS);
    private static final Duration IMAGE_DOWNLOAD_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient kakaoWebClient;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public Mono<Point> searchPointByAsync(final String placeName) {
        final String url = String.format(KakaoMapConstants.SEARCH_POINT_URL, placeName);
        return getDataAsync(url)
                .map(response -> new Point(response.findStationX(), response.findStationY()));
    }

    public Mono<KakaoApiResponse> searchPlacesByAsync(final SearchPlacesRequest request) {
        return searchPlacesByAsync(
                request.query(),
                String.valueOf(request.longitude()),
                String.valueOf(request.latitude()),
                request.radius()
        );
    }

    public Mono<KakaoApiResponse> searchPlacesByAsync(final SearchPlacesLimitQuantityRequest request) {
        return searchPlacesByAsync(
                request.query(),
                request.name(),
                String.valueOf(request.longitude()),
                String.valueOf(request.latitude()),
                request.radius(),
                request.size()
        );
    }

    private Mono<KakaoApiResponse> searchPlacesByAsync(
            final String keyword,
            final String longitude,
            final String latitude,
            final int radius
    ) {
        final String url = String.format(
                KakaoMapConstants.SEARCH_PLACE_URL,
                keyword,
                longitude,
                latitude,
                radius
        );
        return getDataAsync(url);
    }

    private Mono<KakaoApiResponse> searchPlacesByAsync(
            final String keyword,
            final String stationName,
            final String longitude,
            final String latitude,
            final int radius,
            final int size
    ) {
        final String url = String.format(
                KakaoMapConstants.SEARCH_PLACE_WITH_SIZE_URL,
                keyword,
                longitude,
                latitude,
                radius,
                size
        );
        return getDataAsync(url)
                .flatMap(response -> enrichWithImagesAsync(response, stationName));
    }

    private Mono<KakaoApiResponse> getDataAsync(final String url) {
        return kakaoWebClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(KakaoApiResponse.class)
                .timeout(REQUEST_TIMEOUT)
                .onErrorMap(this::mapException);
    }

    public Mono<KakaoImageApiResponse> searchImagesByAsync(final SearchImageRequest request) {
        final String url = String.format(
                KakaoMapConstants.SEARCH_IMAGE_URL,
                request.getQuery(),
                request.getPage(),
                request.getSize()
        );
        return getImageDataAsync(url);
    }

    private Mono<KakaoImageApiResponse> getImageDataAsync(final String url) {
        return kakaoWebClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(KakaoImageApiResponse.class)
                .timeout(REQUEST_TIMEOUT)
                .onErrorMap(this::mapException);
    }

    private Mono<KakaoApiResponse> enrichWithImagesAsync(
            final KakaoApiResponse response,
            final String stationName
    ) {
        final List<Mono<DocumentResponse>> imageEnrichmentMonos = response.documents().stream()
                .map(document -> addImageUrlToDocumentAsync(document, stationName))
                .collect(Collectors.toList());

        if (imageEnrichmentMonos.isEmpty()) {
            return Mono.just(response);
        }

        return Mono.zip(imageEnrichmentMonos, arrays ->
                        response.withImageUrls(
                                java.util.Arrays.stream(arrays)
                                        .map(obj -> (DocumentResponse) obj)
                                        .collect(Collectors.toList())
                        )
                )
                .onErrorResume(e -> {
                    log.warn("Failed to enrich images for station: {}", stationName, e);
                    return Mono.just(response);
                });
    }

    private Mono<DocumentResponse> addImageUrlToDocumentAsync(
            final DocumentResponse document,
            final String stationName
    ) {
        final SearchImageRequest imageRequest = new SearchImageRequest(
                stationName,
                document.placeName(),
                1,
                1
        );

        return searchImagesByAsync(imageRequest)
                .flatMap(imageResponse -> {
                    if (imageResponse.documents() != null && !imageResponse.documents().isEmpty()) {
                        final String imageUrl = imageResponse.documents().get(0).thumbnailUrl();
                        return downloadImageAsBase64Async(imageUrl)
                                .map(base64 -> document.withImageUrl(base64))
                                .onErrorResume(e -> {
                                    log.debug("Failed to download image as base64 for place: {}",
                                            document.placeName(), e);
                                    return Mono.just(document.withImageUrl(null));
                                });
                    }
                    log.debug("No image found for place: {}", document.placeName());
                    return Mono.just(document.withImageUrl(null));
                })
                .onErrorResume(e -> {
                    log.debug("Failed to fetch image for place: {}", document.placeName(), e);
                    return Mono.just(document.withImageUrl(null));
                });
    }

    private Mono<String> downloadImageAsBase64Async(final String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return Mono.empty();
        }

        long startTime = System.currentTimeMillis();

        return kakaoWebClient.get()
                .uri(imageUrl)
                .retrieve()
                .toEntity(byte[].class)
                .timeout(IMAGE_DOWNLOAD_TIMEOUT)
                .map(response -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    byte[] imageBytes = response.getBody();
                    if (imageBytes == null || imageBytes.length == 0) {
                        log.debug("Downloaded empty image in {}ms: {}", elapsed, imageUrl);
                        return null;
                    }
                    log.debug("Image downloaded in {}ms: {}", elapsed, imageUrl);
                    String mediaType = getMediaTypeFromResponse(response);
                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    return "data:" + mediaType + ";base64," + base64;
                })
                .onErrorResume(e -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    log.warn("Image download failed after {}ms: {}", elapsed, imageUrl, e);
                    return Mono.empty();
                });
    }

    private String getMediaTypeFromResponse(
            org.springframework.http.ResponseEntity<byte[]> response) {
        org.springframework.http.MediaType contentType = response.getHeaders().getContentType();
        if (contentType != null) {
            return contentType.toString();
        }
        return "image/jpeg"; // 기본값
    }

    private Throwable mapException(final Throwable throwable) {
        if (throwable instanceof WebClientResponseException wcre) {
            return mapWebClientException(wcre);
        }

        if (throwable instanceof java.util.concurrent.TimeoutException) {
            log.error("Kakao API request timeout");
            return new ExternalApiException(ExternalApiErrorCode.TEMPORARILY_INVALID_KAKAO_MAP_API_RESPONSE);
        }

        log.error("Kakao API request failed", throwable);
        return new ExternalApiException(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
    }

    private ExternalApiException mapWebClientException(final WebClientResponseException wcre) {
        try {
            final byte[] body = wcre.getResponseBodyAsString().getBytes();
            final KakaoMapErrorResponse error = objectMapper.readValue(body, KakaoMapErrorResponse.class);
            log.error("Kakao API error - code: {}, message: {}", error.code(), error.msg());

            if (KakaoMapConstants.ERROR_CODE_CAN_RETRY.contains(error.code())) {
                return new ExternalApiException(
                        ExternalApiErrorCode.TEMPORARILY_INVALID_KAKAO_MAP_API_RESPONSE
                );
            }

            if (KakaoMapConstants.ERROR_CODE_QUOTA_EXCEEDED.equals(error.code())) {
                return new ExternalApiException(
                        ExternalApiErrorCode.EXCEEDED_KAKAO_MAP_API_TOKEN_QUOTA
                );
            }

            return new ExternalApiException(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
        } catch (Exception e) {
            log.error("Failed to parse Kakao API error response", e);
            return new ExternalApiException(ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE);
        }
    }

}
