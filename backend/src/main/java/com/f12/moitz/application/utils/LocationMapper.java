package com.f12.moitz.application.utils;

import com.f12.moitz.application.dto.LocationRecommendResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.domain.Location;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class LocationMapper {

    public List<LocationRecommendResponse> toResponse(List<Location> sortedLocations, Map<Place, String> generatedPlaces) {
        if (sortedLocations.isEmpty()) {
            return List.of();
        }
        final int minTime = sortedLocations.get(0).calculateAverageTravelTime();

        return IntStream.range(0, sortedLocations.size())
                .mapToObj(index -> {
                    Location currentLocation = sortedLocations.get(index);
                    String reason = generatedPlaces.get(currentLocation.getTargetPlace());
                    return toLocationRecommendResponse(currentLocation, index, minTime, reason);
                })
                .toList();
    }

    private LocationRecommendResponse toLocationRecommendResponse(Location location, int index, int minTime, String reason) {
        Place targetPlace = location.getTargetPlace();
        int totalTime = location.calculateAverageTravelTime();

        List<PlaceRecommendResponse> recommendedPlaces = toPlaceRecommendResponses(location.getRecommendedPlaces(), index + 1);
        List<RouteResponse> routes = toRouteResponses(location.getRoutes());

        return new LocationRecommendResponse(
                (long) index + 1,
                index + 1,
                targetPlace.getPoint().getY(),
                targetPlace.getPoint().getX(),
                targetPlace.getName(),
                totalTime,
                totalTime == minTime,
                reason,
                reason,
                recommendedPlaces,
                routes
        );
    }

    private List<PlaceRecommendResponse> toPlaceRecommendResponses(List<RecommendedPlace> places, int rank) {
        return places.stream()
                .map(p -> new PlaceRecommendResponse(
                        rank,
                        p.getPlaceName(),
                        p.getCategory(),
                        p.getWalkingTime(),
                        p.getUrl()
                ))
                .toList();
    }

    private List<RouteResponse> toRouteResponses(List<Route> routes) {
        return routes.stream()
                .map(this::toRouteResponse)
                .toList();
    }

    private RouteResponse toRouteResponse(Route route) {
        List<PathResponse> pathResponses = IntStream.range(0, route.getPaths().size())
                .mapToObj(pathIndex -> toPathResponse(route.getPaths().get(pathIndex), pathIndex + 1))
                .toList();

        return new RouteResponse(
                route.getStartPlace().getName(),
                route.getStartPlace().getPoint().getX(),
                route.getStartPlace().getPoint().getY(),
                route.calculateTransferCount(),
                route.calculateTotalTravelTime(),
                pathResponses
        );
    }

    private PathResponse toPathResponse(Path path, int order) {
        return new PathResponse(
                order,
                path.getStart().getName(),
                path.getStart().getPoint().getX(),
                path.getStart().getPoint().getY(),
                path.getEnd().getName(),
                path.getEnd().getPoint().getX(),
                path.getEnd().getPoint().getY(),
                "호선", // TODO: 실제 호선 정보 매핑
                path.getTravelTime().toMinutesPart()
        );
    }
}
