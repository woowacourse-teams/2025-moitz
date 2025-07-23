package com.f12.moitz.application;

import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.LocationRecommendResponse;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.BriefRecommendedLocationResponse;
import com.f12.moitz.infrastructure.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.odsay.OdsayClient;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;
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

    public List<LocationRecommendResponse> recommendLocation(final LocationRecommendRequest request) {
        final List<LocationNameAndReason> generatedLocations = generateLocations(request);
        final List<Place> startingPlace = getPlacesByName(request.stations());
        final List<String> placeNames = generatedLocations.stream()
                .map(LocationNameAndReason::locationName)
                .toList();
        final List<Place> recommendedPlaceByAi = getPlacesByName(placeNames);
        return verifyRecommendLocationsByTravelTime(startingPlace, recommendedPlaceByAi, generatedLocations);
    }

    private List<LocationNameAndReason> generateLocations(final LocationRecommendRequest request) {
        final BriefRecommendedLocationResponse briefRecommendedLocationResponse = googleGeminiClient.generateBriefResponse(
                request.stations(),
                request.additionalCondition()
        );
        log.info("카테고리: {}", briefRecommendedLocationResponse.additionalConditionsCategoryCodes());
        return briefRecommendedLocationResponse.recommendations();
    }

    private List<Place> getPlacesByName(final List<String> locationNames) {
        return locationNames.stream()
                .map(locationName -> new Place(
                        locationName,
                        kakaoMapClient.searchPointBy(locationName)
                ))
                .toList();
    }

    private List<LocationRecommendResponse> verifyRecommendLocationsByTravelTime(
            List<Place> startingLocations,
            List<Place> locations,
            List<LocationNameAndReason> generatedLocations
    ) {
        Map<Place, Integer> verifiedLocations = new HashMap<>();

        for (Place location : locations) {
            List<Integer> durations = new ArrayList<>();
            for (Place startingLocation : startingLocations) {
                final SubwayRouteSearchResponse route = getSubwayRouteSearchResponse(location, startingLocation);
                final int durationTime = route.getLeastTime();
                durations.add(durationTime);
            }

            if (isInappropriatePlace(durations)) {
                continue;
            }

            int averageTime = (int) durations.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElseThrow(() -> new IllegalStateException("Average duration not found"));

            verifiedLocations.put(location, averageTime);
        }

        return sortAndParseResponse(generatedLocations, verifiedLocations);
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

        return max - min > avg * 1.5;
    }

    private List<LocationRecommendResponse> sortAndParseResponse(
            final List<LocationNameAndReason> generatedLocations,
            final Map<Place, Integer> verifiedLocations
    ) {
        int minTime = verifiedLocations.values().stream()
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("No minimum time found"));

        List<Map.Entry<Place, Integer>> entryList = new ArrayList<>(verifiedLocations.entrySet());
        entryList.sort(Entry.comparingByValue());

        return IntStream.range(0, entryList.size())
                .mapToObj(index -> {
                    Map.Entry<Place, Integer> placeIntegerEntry = entryList.get(index);
                    String locationName = placeIntegerEntry.getKey().getName();

                    String reason = generatedLocations.stream()
                            .filter(generatedLocation -> generatedLocation.locationName().equals(locationName))
                            .findFirst()
                            .map(LocationNameAndReason::reason)
                            .orElse(null);

                    return new LocationRecommendResponse(
                            index + 1,
                            placeIntegerEntry.getKey().getPoint().getY(),
                            placeIntegerEntry.getKey().getPoint().getX(),
                            locationName,
                            placeIntegerEntry.getValue(),
                            // 만약 최소 시간이 같은 경우가 있다면?
                            minTime == placeIntegerEntry.getValue(),
                            reason
                    );
                })
                .toList();
    }

}
