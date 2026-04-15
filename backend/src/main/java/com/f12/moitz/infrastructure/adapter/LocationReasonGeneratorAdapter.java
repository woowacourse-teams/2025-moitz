package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.perplexity.PerplexityClient;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationReasonGeneratorAdapter implements LocationReasonGenerator {

    private final GoogleGeminiClient googleGeminiClient;
    private final PerplexityClient perplexityClient;
    private final CircuitBreaker geminiBreaker;
    private final CircuitBreaker geminiRetryableBreaker;

    @Retryable(
            retryFor = RetryableApiException.class,
            maxAttempts = 2,
            recover = "recoverGenerateReasons"
    )
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
        final Supplier<Map<String, ReasonAndDescription>> geminiCall = () -> mergeWithFallback(
                selectedPlaces,
                googleGeminiClient.generateReasonsForSelectedLocations(
                        startingPlaces,
                        selectedPlaces,
                        requirementStrings
                )
        );

        return Decorators.ofSupplier(geminiCall)
                .withCircuitBreaker(geminiBreaker)
                .withCircuitBreaker(geminiRetryableBreaker)
                .withFallback(
                        List.of(ExternalApiException.class, CallNotPermittedException.class),
                        throwable -> fallback(startingPlaces, selectedPlaces, requirementStrings)
                )
                .decorate()
                .get();
    }

    @Recover
    public Map<String, ReasonAndDescription> recoverGenerateReasons(
            final List<String> startingPlaces,
            final List<String> selectedPlaces,
            final List<RecommendCondition> requirements
    ) {
        final List<String> requirementStrings = RecommendCondition.getRequirements(requirements);
        return fallback(startingPlaces, selectedPlaces, requirementStrings);
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

    private Map<String, ReasonAndDescription> fallback(
            final List<String> startingPlaces,
            final List<String> selectedPlaces,
            final List<String> requirements
    ) {
        try {
            log.debug("Gemini 추천 이유 생성 실패. Perplexity fallback을 시도합니다.");
            final RecommendedLocationsResponse response = perplexityClient.generateReasonsForSelectedLocations(
                    startingPlaces,
                    selectedPlaces,
                    requirements
            );
            return mergeWithFallback(selectedPlaces, response);
        } catch (ExternalApiException | RetryableApiException e) {
            log.warn("Perplexity 추천 이유 생성도 실패했습니다. 고정 fallback 문구를 사용합니다.", e);
            return createFallbackReasons(selectedPlaces);
        }
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
