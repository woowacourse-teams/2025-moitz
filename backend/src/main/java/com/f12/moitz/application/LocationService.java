package com.f12.moitz.application;

import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.PlaceResponse;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationPreview;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.odsay.OdsayClient;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationService {

    private final GoogleGeminiClient googleGeminiClient;
    private final OdsayClient odsayClient;
    private final KakaoMapClient kakaoMapClient;

    public List<PlaceResponse> recommendLocation(final LocationRecommendRequest request) {
        final List<String> locationNames = generateLocationNames(request);
        final List<Place> startingPlace = getPlacesByName(request.stations());
        final List<Place> recommendedPlaceByAi = getPlacesByName(locationNames);
        return verifyRecommendLocationsByTravelTime(startingPlace, recommendedPlaceByAi).stream()
                .map(place -> new PlaceResponse(
                        place.getPoint().getY(),
                        place.getPoint().getX(),
                        place.getName()
                ))
                .toList();
    }

    private List<String> generateLocationNames(final LocationRecommendRequest request) {
        final RecommendedLocationPreview recommendedLocationPreview = googleGeminiClient.generateBriefResponse(
                request.stations(),
                request.additionalCondition()
        );
        log.info("카테고리: {}", recommendedLocationPreview.additionalConditionsCategoryCodes());
        return recommendedLocationPreview.recommendations();
    }

    private List<Place> getPlacesByName(final List<String> locationNames) {
        return locationNames.stream()
                .map(locationName -> new Place(
                        locationName,
                        kakaoMapClient.searchPointBy(locationName)
                ))
                .toList();
    }

    private List<Place> verifyRecommendLocationsByTravelTime(List<Place> startingLocations, List<Place> locations) {
        List<Place> verifiedLocations = new ArrayList<>();

        for (Place location : locations) {
            List<Integer> durations = new ArrayList<>();
            for (Place startingLocation : startingLocations) {
                final SubwayRouteSearchResponse route = getSubwayRouteSearchResponse(location,
                        startingLocation);
                final int durationTime = route.getLeastTime();
                durations.add(durationTime);
            }

            if (isInappropriatePlace(durations)) {
                log.info("기준 미충족: {}}", location.getName());
                continue;
            }

            verifiedLocations.add(location);
        }

        return verifiedLocations;
    }

    private SubwayRouteSearchResponse getSubwayRouteSearchResponse(final Place location, final Place startingLocation) {
        final SubwayRouteSearchResponse route = odsayClient.getRoute(
                startingLocation.getPoint(),
                location.getPoint()
        );
        if (route.result() == null && route.error().isEmpty()) {
            throw new RuntimeException("이동 경로를 찾을 수 없습니다. " +
                    "출발지: " + startingLocation.getName() + ", 목적지: " + location.getName());
        }
        return route;
    }

    private boolean isInappropriatePlace(final List<Integer> durations) {
        int max = durations.stream()
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Max duration not found"));

        int min = durations.stream()
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Min duration not found"));

        double avg = durations.stream().mapToInt(Integer::intValue)
                .average()
                .orElseThrow(() -> new IllegalStateException("Average duration not found"));

        return max - min > avg * 0.8;
    }

}
