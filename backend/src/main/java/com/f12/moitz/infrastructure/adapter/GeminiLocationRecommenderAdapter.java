package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.client.gpt.GptClient;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.f12.moitz.infrastructure.PromptGenerator.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiLocationRecommenderAdapter implements LocationRecommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;
    private final GptClient gptClient;
    private final PlaceFinder placeFinder;
    private List<CompletableFuture<Map.Entry<Place, List<RecommendedPlace>>>> futures;


    @Retryable(
            retryFor = RetryableApiException.class,
            maxAttempts = 2,
            recover = "recoverRecommendedLocations"
    )
    @Override
    public RecommendedLocationResponse getRecommendedLocations(
            final List<String> startPlaceNames,
            final String condition
    ) {
        return geminiClient.generateResponse(
                startPlaceNames,
                condition
        );
    }

    @Recover
    public RecommendedLocationResponse recoverRecommendedLocations(
            final List<String> startPlaceNames,
            final String condition
    ) {
        return gptClient.generateResponse(
                startPlaceNames,
                condition
        );
    }

    @Override
    public Map<Place, String> recommendLocations(RecommendedLocationResponse recommendedLocationResponse) {
        return recommendedLocationResponse.recommendations().stream()
                .collect(Collectors.toMap(recommendation ->
                        placeFinder.findPlaceByName(recommendation.locationName()), LocationNameAndReason::reason
                ));
    }

    @Override
    public Map<Place, List<RecommendedPlace>> recommendPlaces(final List<Place> targets, final String requirement) {
        Map<Place, List<KakaoApiResponse>> searchedAllPlaces = searchPlacesWithRequirement(targets, requirement);

        List<CompletableFuture<Map.Entry<Place, List<RecommendedPlace>>>> futures = searchedAllPlaces.entrySet()
                .stream()
                .map(entry -> processPlaceFilteringAsync(entry.getKey(), entry.getValue(), requirement))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 개별 장소의 카카오맵 응답을 Gemini로 필터링하는 비동기 처리
     */
    @Async("asyncTaskExecutor")
    public CompletableFuture<Map.Entry<Place, List<RecommendedPlace>>> processPlaceFilteringAsync(
            final Place place,
            final List<KakaoApiResponse> kakaoResponses,
            final String requirement) {

        try {
            String formattedKakaoData = FORMAT_SINGLE_PLACE_TO_PROMPT(place, kakaoResponses);

            // 2. Gemini 필터링 프롬프트 생성 (기존 스타일 적용)
            String prompt = String.format(
                    PLACE_FILTER_PROMPT,
                    place.getName(),
                    PLACE_RECOMMENDATION_COUNT,
                    place.getName(),
                    place.getPoint().getX(),
                    place.getPoint().getY(),
                    requirement,
                    formattedKakaoData,
                    PLACE_RECOMMENDATION_COUNT
            );

            // 3. Gemini에게 필터링 요청
            List<PlaceRecommendResponse> filteredResponses = geminiClient.generateWith(prompt);

            // 4. RecommendedPlace 객체로 변환
            List<RecommendedPlace> recommendedPlaces = filteredResponses.stream()
                    .map(response -> new RecommendedPlace(
                            response.name(),
                            response.category(),
                            response.walkingTime(),
                            response.url()
                    ))
                    .toList();

            return CompletableFuture.completedFuture(Map.entry(place, recommendedPlaces));
        } catch (Exception e) {
            log.error("Error filtering places for: {}", place.getName(), e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
        }
    }

    private Map<Place, List<KakaoApiResponse>> searchPlacesWithRequirement(final List<Place> targets, final String requirement) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
                            KakaoApiResponse response = kakaoMapClient.searchPlacesBy(
                                    new SearchPlacesRequest(
                                            requirement,
                                            place.getPoint().getX(),
                                            place.getPoint().getY(),
                                            800
                                    )
                            );
                            return List.of(response);
                        }
                ));
    }

    /**
     * 카카오 맵 데이터를 toString()으로 간단하게 포맷팅
     */
    private String formatKakaoDataForPrompt(Map<Place, List<KakaoApiResponse>> searchedAllPlaces) {
        StringBuilder sb = new StringBuilder();

        sb.append("KAKAO MAP SEARCH RESULTS:\n");
        sb.append("========================\n\n");

        searchedAllPlaces.forEach((place, kakaoResponseList) -> {
            // 역명에 "역"이 없으면 추가하여 AI가 일관된 형식으로 응답하도록 함
            String stationName = place.getName();
            if (!stationName.endsWith("역")) {
                stationName = stationName + "역";
            }

            sb.append(String.format("=== %s ===\n", stationName));
            sb.append(String.format("Station Coordinates: (%.6f, %.6f)\n",
                    place.getPoint().getX(), place.getPoint().getY()));
            sb.append("Search Results:\n");

            for (int i = 0; i < kakaoResponseList.size(); i++) {
                KakaoApiResponse response = kakaoResponseList.get(i);
                sb.append(String.format("Response %d: %s\n", i + 1, response.toString()));
            }

            sb.append("\n");
        });

        sb.append("========================\n");
        sb.append("Please analyze the above Kakao Map data and select the best places that match the user requirement.\n");
        sb.append("Focus on places that are NOT subway stations (지하철역) and are relevant to the user's needs.\n");
        sb.append("IMPORTANT: Use the exact station names shown above (with '역' suffix) in your response.\n\n");

        log.info("log = {}" , sb.toString());
        return sb.toString();
    }

    /**
     * 역명 매칭을 유연하게 처리하는 메서드
     * AI 응답에서 "역"이 붙은 형태로 나오므로 이에 맞춰 매칭
     */

    /**
     * 두 좌표 간의 거리를 미터 단위로 계산 (Haversine formula)
     */
    private double calculateDistance(Point point1, Point point2) {
        double lat1 = Math.toRadians(point1.getY());
        double lon1 = Math.toRadians(point1.getX());
        double lat2 = Math.toRadians(point2.getY());
        double lon2 = Math.toRadians(point2.getX());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon/2) * Math.sin(dlon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double earthRadius = 6371000; // 지구 반지름 (미터)

        return earthRadius * c;
    }

}
