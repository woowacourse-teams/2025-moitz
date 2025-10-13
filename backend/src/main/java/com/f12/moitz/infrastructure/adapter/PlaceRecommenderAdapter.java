package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponses;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceRecommenderAdapter implements PlaceRecommender {

    private final KakaoMapClient kakaoMapClient;

    @Override
    public Map<Place, CategorizedRecommendedPlaces> recommendPlaces(
            final List<Place> targetPlaces,
            final List<String> requirements
    ) {
        final Map<Place, KakaoApiResponses> searchResults = searchPlacesWithRequirement(targetPlaces, requirements);

        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new CategorizedRecommendedPlaces(
                                entry.getValue()
                                        .kakaoApiResponses()
                                        .entrySet().stream()
                                        .collect(Collectors.toMap(
                                                reqEntry -> RecommendCondition.fromTitle(reqEntry.getKey()),
                                                reqEntry -> reqEntry.getValue().stream()
                                                        .flatMap(resp -> resp.documents().stream())
                                                        .map(document -> new RecommendedPlace(
                                                                document.placeName(),
                                                                new Point(Double.parseDouble(document.x()), Double.parseDouble(document.y())),
                                                                parseCategoryName(document.categoryName()),
                                                                calculateWalkingTime(Integer.parseInt(document.distance())),
                                                                document.placeUrl()
                                                        ))
                                                        .toList()
                                        ))
                        )
                ));

    }

    private Map<Place, KakaoApiResponses> searchPlacesWithRequirement(final List<Place> targets, final List<String> requirements) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
                            Map<String,List<KakaoApiResponse>> responsesByCategory =
                                    requirements.stream().collect(Collectors.toMap(
                                            requirement -> RecommendCondition.fromKeyword(requirement).getTitle(),
                                            requirement -> {
                                                KakaoApiResponse response = kakaoMapClient.searchPlacesBy(
                                                        new SearchPlacesLimitQuantityRequest(
                                                                requirement,
                                                                place.getPoint().getX(),
                                                                place.getPoint().getY(),
                                                                800,
                                                                3
                                                        )
                                                );
                                                return List.of(response);
                                            }
                                    ));
                            return new KakaoApiResponses(responsesByCategory);
                        }
                ));
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