package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.AsyncPlaceRecommender;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapAsyncClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceRecommenderAsyncAdapter implements AsyncPlaceRecommender {

    private final KakaoMapAsyncClient kakaoMapAsyncClient;

    public Mono<Map<Place, CategorizedRecommendedPlaces>> recommendPlacesAsync(
            final List<Place> targetPlaces,
            final List<String> requirements
    ) {
        return searchPlacesWithRequirementAsync(targetPlaces, requirements)
                .map(this::buildCategorizedRecommendedPlaces);
    }

    private Map<Place, CategorizedRecommendedPlaces> buildCategorizedRecommendedPlaces(
            final Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>> searchResults
    ) {
        return searchResults.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Entry::getKey,
                        entry -> new CategorizedRecommendedPlaces(
                                buildCategoryMap(entry.getValue())
                        )
                ));
    }

    private Map<RecommendCondition, List<RecommendedPlace>> buildCategoryMap(
            final Map<RecommendCondition, List<KakaoApiResponse>> categoryResponses
    ) {
        return categoryResponses.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().stream()
                                .flatMap(resp -> resp.documents().stream())
                                .map(this::toRecommendedPlace)
                                .toList()
                ));
    }

    private RecommendedPlace toRecommendedPlace(
            final com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse document
    ) {
        return new RecommendedPlace(
                document.placeName(),
                new Point(
                        Double.parseDouble(document.x()),
                        Double.parseDouble(document.y())
                ),
                parseCategoryName(document.categoryName()),
                calculateWalkingTime(Integer.parseInt(document.distance())),
                document.placeUrl(),
                document.imageUrl()
        );
    }

    private Mono<Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>>> searchPlacesWithRequirementAsync(
            final List<Place> targetPlaces,
            final List<String> requirements
    ) {
        return Flux.fromIterable(targetPlaces)
                .flatMap(place -> searchRequirementsForPlaceAsync(place, requirements)
                        .map(requirementMap -> Map.entry(place, requirementMap))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private Mono<Map<RecommendCondition, List<KakaoApiResponse>>> searchRequirementsForPlaceAsync(
            final Place place,
            final List<String> requirements
    ) {
        return Flux.fromIterable(requirements)
                .flatMap(requirement -> kakaoMapAsyncClient.searchPlacesByAsync(
                        new SearchPlacesLimitQuantityRequest(
                                requirement,
                                place.getName(),
                                place.getPoint().getX(),
                                place.getPoint().getY(),
                                800,
                                3
                        )
                )
                        .map(response -> Map.entry(
                                RecommendCondition.fromKeyword(requirement),
                                new ArrayList<>(List.of(response))
                        ))
                        .doOnError(e -> log.warn(
                                "Failed to search places for requirement: {} at place: {}",
                                requirement, place.getName(), e
                        ))
                        .onErrorResume(e -> {
                            log.debug("Skipping requirement {} due to error", requirement);
                            return Mono.empty();
                        })
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue, java.util.HashMap::new);
    }

    private int calculateWalkingTime(final int distance) {
        return Math.toIntExact(Math.round((double) distance / 100 * 1.5));
    }

    private String parseCategoryName(final String categoryName) {
        final String regex = ">";
        if (!categoryName.contains(regex)) {
            return categoryName;
        }
        final List<String> tokens = Arrays.stream(categoryName.split(regex))
                .map(String::trim)
                .toList();
        return tokens.getLast();
    }

}
