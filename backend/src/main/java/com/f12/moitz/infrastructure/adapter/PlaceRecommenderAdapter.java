package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponses;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceRecommenderAdapter implements PlaceRecommender {

    private final KakaoMapClient kakaoMapClient;

    @Override
    public Map<Place, CategorizedRecommendedPlaces> recommendPlaces(
            final List<Place> targetPlaces,
            final List<RecommendCondition> requirements
    ) {
        final Map<Place, KakaoApiResponses> searchResults = searchPlacesWithRequirement(targetPlaces, requirements);
        return buildCategorizedRecommendedPlaces(searchResults);
    }

    private Map<Place, CategorizedRecommendedPlaces> buildCategorizedRecommendedPlaces(
            final Map<Place, KakaoApiResponses> searchResults
    ) {
        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new CategorizedRecommendedPlaces(
                                buildCategoryMap(entry.getValue().kakaoApiResponses())
                        )
                ));
    }

    private Map<RecommendCondition, List<RecommendedPlace>> buildCategoryMap(
            final Map<RecommendCondition, List<KakaoApiResponse>> categoryResponses
    ) {
        return categoryResponses.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().stream()
                                .flatMap(resp -> resp.documents().stream())
                                .map(this::toRecommendedPlace)
                                .toList()
                ));
    }

    private RecommendedPlace toRecommendedPlace(final DocumentResponse document) {
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

    private Map<Place, KakaoApiResponses> searchPlacesWithRequirement(
            final List<Place> targets,
            final List<RecommendCondition> requirements
    ) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
                            Map<RecommendCondition, List<KakaoApiResponse>> responsesByCategory =
                                    requirements.stream().collect(Collectors.toMap(
                                            condition -> condition,
                                            condition -> searchKeywordsForCondition(place, condition),
                                            (existing, incoming) -> {
                                                existing.addAll(incoming);
                                                return existing;
                                            }
                                    ));
                            return new KakaoApiResponses(responsesByCategory);
                        }
                ));
    }

    private List<KakaoApiResponse> searchKeywordsForCondition(
            final Place place,
            final RecommendCondition condition
    ) {
        List<KakaoApiResponse> allResponses = new ArrayList<>();
        for (String keyword : condition.getKeywords()) {
            try {
                KakaoApiResponse response = kakaoMapClient.searchPlacesBy(
                        new SearchPlacesLimitQuantityRequest(
                                keyword,
                                place.getName(),
                                place.getPoint().getX(),
                                place.getPoint().getY(),
                                800,
                                3
                        )
                );
                allResponses.add(response);
            } catch (Exception e) {
                log.warn(
                        "Failed to search places for keyword: {} at place: {}",
                        keyword, place.getName(), e
                );
            }
        }
        return allResponses;
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
