package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.gemini.GeminiRecommendPlaceClient;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceRecommenderAdapter implements PlaceRecommender {

    private final KakaoMapClient kakaoMapClient;
    private final GeminiRecommendPlaceClient geminiClient;

    @Override
    public Place findPlaceByName(final String placeName) {
        return new Place(
                placeName,
                kakaoMapClient.searchPointBy(placeName)
        );
    }

    @Override
    public List<Place> findPlacesByNames(final List<String> placeNames) {
        return placeNames.stream()
                .map(placeName -> new Place(
                        placeName,
                        kakaoMapClient.searchPointBy(placeName)
                ))
                .toList();
    }

    @Override
    public Map<Place, List<RecommendedPlace>> recommendPlaces(final Set<Place> targetPlaces, final String requirement) {
        return geminiClient.generateWithParallelFunctionCalling(targetPlaces, requirement)
                .entrySet().stream()
                .map(entry -> {
                    Place place = entry.getKey();
                    List<RecommendedPlace> recommendedPlaces = entry.getValue().stream()
                            .map(recommendedPlace -> new RecommendedPlace(
                                    recommendedPlace.name(),
                                    recommendedPlace.category(),
                                    recommendedPlace.walkingTime(),
                                    recommendedPlace.url()
                            )).toList();
                    return Map.entry(place, recommendedPlaces);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
