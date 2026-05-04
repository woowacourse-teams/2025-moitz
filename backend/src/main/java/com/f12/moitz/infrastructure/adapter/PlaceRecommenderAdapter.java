package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponses;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import com.f12.moitz.infrastructure.utils.KakaoPlaceMapper;
import java.util.ArrayList;
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

    private static final int PLACE_RECOMMENDATION_COUNT = 5;

    private final KakaoMapClient kakaoMapClient;
    private final KakaoPlaceMapper kakaoPlaceMapper;

    @Override
    public Map<Place, RecommendedPlaces> recommendPlaces(
            final List<Place> targetPlaces,
            final List<RecommendCondition> requirements
    ) {
        final Map<Place, KakaoApiResponses> searchResults = searchPlacesWithRequirement(targetPlaces, requirements);
        return buildRecommendedPlaces(searchResults);
    }

    private Map<Place, RecommendedPlaces> buildRecommendedPlaces(
            final Map<Place, KakaoApiResponses> searchResults
    ) {
        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new RecommendedPlaces(
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
                                .map(kakaoPlaceMapper::toRecommendedPlace)
                                .toList()
                ));
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
                                PLACE_RECOMMENDATION_COUNT
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

}
