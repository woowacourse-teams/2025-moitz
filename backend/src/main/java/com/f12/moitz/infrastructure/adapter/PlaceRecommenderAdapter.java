package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceRecommenderAdapter implements PlaceRecommender {

    private final KakaoMapClient kakaoMapClient;

    @Override
    public Map<Place, List<RecommendedPlace>> recommendPlaces(
            final List<Place> targetPlaces,
            final String requirement
    ) {
        final Map<Place, List<KakaoApiResponse>> searchResults = searchPlacesWithRequirement(targetPlaces, requirement);
        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .flatMap(response -> response.documents().stream())
                                .map(document -> new RecommendedPlace(
                                        document.placeName(),
                                        parseCategoryName(document.categoryName()),
                                        calculateWalkingTime(Integer.parseInt(document.distance())),
                                        document.placeUrl()
                                ))
                                .collect(Collectors.toList())
                ));
    }

    private Map<Place, List<KakaoApiResponse>> searchPlacesWithRequirement(final List<Place> targets, final String requirement) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
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

