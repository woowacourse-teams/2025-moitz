package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.Recommender;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.gpt.GptClient;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.kakao.dto.SearchPlacesRequest;
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
public class GeminiRecommenderAdapter implements Recommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;
    private GptClient gptClient;
    private List<CompletableFuture<Map.Entry<Place, List<RecommendedPlace>>>> futures;

    @Override
    public Place findPlaceByName(final String placeName) {
        return new Place(placeName, kakaoMapClient.searchPointBy(placeName));
    }

    @Override
    public List<Place> findPlacesByNames(final List<String> placeNames) {
        return placeNames.stream()
                .map(this::findPlaceByName)
                .toList();
    }

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
                        findPlaceByName(recommendation.locationName()), LocationNameAndReason::reason
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
    @Async("placeRecommendationExecutor")
    private CompletableFuture<Map.Entry<Place, List<RecommendedPlace>>> processPlaceFilteringAsync(
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

}