package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationReasonGeneratorAdapter implements LocationReasonGenerator {

    private final GoogleGeminiClient googleGeminiClient;

    @Override
    public Map<String, ReasonAndDescription> generateReasons(
            final List<String> startingPlaces,
            final List<String> selectedPlaces,
            final List<RecommendCondition> requirements
    ) {
        if (selectedPlaces.isEmpty()) {
            return Map.of();
        }

        final List<String> requirementStrings = RecommendCondition.getRequirements(requirements);
        try {
            final RecommendedLocationsResponse response = googleGeminiClient.generateReasonsForSelectedLocations(
                    startingPlaces,
                    selectedPlaces,
                    requirementStrings
            );
            return mergeWithFallback(selectedPlaces, response);
        } catch (ExternalApiException | RetryableApiException | CallNotPermittedException e) {
            return createFallbackReasons(selectedPlaces);
        }
    }

    private Map<String, ReasonAndDescription> mergeWithFallback(
            final List<String> selectedPlaces,
            final RecommendedLocationsResponse response
    ) {
        final Map<String, ReasonAndDescription> reasons = new LinkedHashMap<>(createFallbackReasons(selectedPlaces));

        response.recommendations().stream()
                .filter(recommendation -> selectedPlaces.contains(recommendation.locationName()))
                .forEach(recommendation -> reasons.put(
                        recommendation.locationName(),
                        new ReasonAndDescription(recommendation.reason(), recommendation.description())
                ));

        return reasons;
    }

    private Map<String, ReasonAndDescription> createFallbackReasons(final List<String> selectedPlaces) {
        final Map<String, ReasonAndDescription> fallback = new LinkedHashMap<>();
        selectedPlaces.forEach(placeName -> fallback.put(
                placeName,
                new ReasonAndDescription(
                        "이동시간과 환승 부담을 함께 고려했을 때 균형이 좋은 만남 장소입니다.",
                        "공평한 만남 장소 📍"
                )
        ));
        return fallback;
    }
}
