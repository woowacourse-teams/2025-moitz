package com.f12.moitz.application;

import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.LocationRecommendResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.odsay.OdsayClient;
import com.f12.moitz.infrastructure.odsay.OdsayMapper;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationService {
    private final GoogleGeminiClient geminiClient;

    private final OdsayClient odsayClient;
    private final KakaoMapClient kakaoMapClient;
    private final OdsayMapper odsayMapper;

    public List<LocationRecommendResponse> recommendLocation(final LocationRecommendRequest request) {
        final List<LocationNameAndReason> aiGeneratedResponse = generateAiRecommendedLocations(request);
        final List<Place> startingPlace = getPlacesByName(request.startingPlaceNames());
        final List<String> placeNames = aiGeneratedResponse.stream()
                .map(LocationNameAndReason::locationName)
                .toList();
        final List<Place> aiGeneratedPlaces = getPlacesByName(placeNames);
        return getAppropriateLocations(startingPlace, aiGeneratedPlaces, aiGeneratedResponse);
    }

    private List<LocationNameAndReason> generateAiRecommendedLocations(final LocationRecommendRequest request) {
        final RecommendedLocationResponse recommendedLocationResponse = geminiClient.generateResponse(
                request.startingPlaceNames(),
                request.requirement()
        );
        return recommendedLocationResponse.recommendations();
    }

    private List<Place> getPlacesByName(final List<String> startingPlaceNames) {
        return startingPlaceNames.stream()
                .map(placeName -> new Place(
                        placeName,
                        kakaoMapClient.searchPointBy(placeName)
                ))
                .toList();
    }

    private List<LocationRecommendResponse> getAppropriateLocations(
            List<Place> startingPlaces,
            List<Place> aiGeneratedPlaces,
            List<LocationNameAndReason> generatedLocations
    ) {
        Map<Place, List<RouteResponse>> arrivalPlaces = new HashMap<>();

        for (Place aiGeneratedPlace : aiGeneratedPlaces) {
            List<RouteResponse> routes = getRoutes(startingPlaces, aiGeneratedPlace);
            List<Integer> durations = routes.stream()
                    .map(RouteResponse::totalTravelTime)
                    .toList();

            if (isInappropriateLocation(durations)) {
                continue;
            }

            arrivalPlaces.put(aiGeneratedPlace, routes);
        }

        return sortAndParseResponse(generatedLocations, arrivalPlaces);
    }

    private List<RouteResponse> getRoutes(final List<Place> startingPlaces, final Place aiGeneratedPlace) {
        List<RouteResponse> routes = new ArrayList<>();
        for (Place startingPlace : startingPlaces) {
            final SubwayRouteSearchResponse route = getSubwayRouteSearchResponse(aiGeneratedPlace, startingPlace);
            routes.add(odsayMapper.toRouteResponse(route));
        }

        return routes;
    }

    private SubwayRouteSearchResponse getSubwayRouteSearchResponse(final Place location, final Place startingLocation) {
        final SubwayRouteSearchResponse route = odsayClient.getRoute(
                startingLocation.getPoint(),
                location.getPoint()
        );
        if (route.result() == null) {
            throw new RuntimeException("이동 경로를 찾을 수 없습니다. " +
                    "출발지: " + startingLocation.getName() + ", 목적지: " + location.getName());
        }
        return route;
    }

    private boolean isInappropriateLocation(final List<Integer> durations) {
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
            final Map<Place, List<RouteResponse>> routes
    ) {
        Map<Place, Integer> averageTimes = new HashMap<>();

        for (Map.Entry<Place, List<RouteResponse>> entry : routes.entrySet()) {
            int averageTime = (int) entry.getValue().stream()
                    .mapToInt(RouteResponse::totalTravelTime)
                    .average()
                    .orElseThrow(() -> new IllegalStateException("Average duration not found"));
            averageTimes.put(entry.getKey(), averageTime);
        }

        int minTime = averageTimes.values().stream()
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("No minimum time found"));

        List<Map.Entry<Place, Integer>> sortedPlaces = new ArrayList<>(averageTimes.entrySet());
        sortedPlaces.sort(Map.Entry.comparingByValue());

        return IntStream.range(0, sortedPlaces.size())
                .mapToObj(index -> {
                    Map.Entry<Place, Integer> entry = sortedPlaces.get(index);
                    Place place = entry.getKey();
                    int totalTime = entry.getValue();
                    String reason = generatedLocations.stream()
                            .filter(gl -> gl.locationName().equals(place.getName()))
                            .findFirst()
                            .map(LocationNameAndReason::reason)
                            .orElse(null);

                    return new LocationRecommendResponse(
                            (long) index + 1,
                            index + 1,
                            place.getPoint().getY(),
                            place.getPoint().getX(),
                            place.getName(),
                            totalTime,
                            // 만약 최소 시간이 같은 경우가 있다면?
                            totalTime == minTime,
                            reason,
                            reason,
                            null,
                            routes.get(place)
                    );
                })
                .toList();
    }

}
