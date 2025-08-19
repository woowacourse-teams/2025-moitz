package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.client.perplexity.PerplexityClient;
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
public class LocationRecommenderAdapter implements LocationRecommender {

    private final GoogleGeminiClient geminiClient;
    private final PerplexityClient perplexityClient;

    private final PlaceFinder placeFinder;

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
        return perplexityClient.generateResponse(
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

}
