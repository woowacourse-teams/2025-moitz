package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.perplexity.PerplexityClient;
import java.util.List;
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

    @Retryable(
            retryFor = RetryableApiException.class,
            maxAttempts = 2,
            recover = "recoverRecommendedLocations"
    )
    @Override
    public RecommendedLocationsResponse recommendLocations(
            final List<String> startPlaceNames,
            final String condition
    ) {
        final RecommendedLocationsResponse generatedResponse = geminiClient.generateResponse(
                startPlaceNames,
                condition
        );
        final RecommendedLocationsResponse deduplicatedLocations = deduplicateLocation(generatedResponse);
        return excludeStartPlaces(
                deduplicatedLocations,
                startPlaceNames
        );
    }

    @Recover
    public RecommendedLocationsResponse recoverRecommendedLocations(
            final List<String> startPlaceNames,
            final String condition
    ) {
        final RecommendedLocationsResponse generatedResponse = perplexityClient.generateResponse(
                startPlaceNames,
                condition
        );
        final RecommendedLocationsResponse deduplicatedLocations = deduplicateLocation(generatedResponse);
        return excludeStartPlaces(
                deduplicatedLocations,
                startPlaceNames
        );
    }

    private RecommendedLocationsResponse deduplicateLocation(
            final RecommendedLocationsResponse response
    ) {
        return new RecommendedLocationsResponse(
                response.recommendations().stream()
                        .distinct()
                        .toList()
        );
    }

    private RecommendedLocationsResponse excludeStartPlaces(
            final RecommendedLocationsResponse response,
            final List<String> startPlaceNames
    ) {
        return new RecommendedLocationsResponse(
                response.recommendations().stream()
                .filter(recommendation -> !startPlaceNames.contains(recommendation.locationName()))
                .toList()
        );
    }

}
