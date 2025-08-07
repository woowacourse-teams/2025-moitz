package com.f12.moitz.infrastructure.adapter;

import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMEND_PROMPT;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.Recommender;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.gemini.GeminiFunctionCaller;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.gpt.GptClient;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiRecommenderAdapter implements Recommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;
    private final GeminiFunctionCaller geminiFunctionCaller;
    private GptClient gptClient;

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
        String targetPlaces = targets.stream()
                .map(place -> String.format(
                        "%s(x=%f, y=%f)", place.getName(), place.getPoint().getX(), place.getPoint().getY()
                ))
                .collect(Collectors.joining(", "));

        String prompt = String.format(PLACE_RECOMMEND_PROMPT, PLACE_RECOMMENDATION_COUNT, targetPlaces, requirement);

        Map<String, List<PlaceRecommendResponse>> responses = geminiFunctionCaller.generateWith(prompt, geminiClient);

        return targets.stream()
                .map(place -> Map.entry(
                                place,
                                responses.get(place.getName()).stream()
                                        .map(response -> new RecommendedPlace(
                                                response.name(),
                                                response.category(),
                                                response.walkingTime(),
                                                response.url()
                                        )).toList()
                        )
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
