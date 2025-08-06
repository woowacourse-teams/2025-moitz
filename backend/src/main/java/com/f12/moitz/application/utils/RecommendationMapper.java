package com.f12.moitz.application.utils;

import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class RecommendationMapper {

    public List<RecommendationResponse> toResponse(
            final Recommendation recommendation,
            final Map<Place, String> generatedPlaces
    ) {
        final int minTime = recommendation.getBestRecommendationTime();

        return IntStream.range(0, recommendation.size())
                .mapToObj(index -> {
                    Candidate currentCandidate = recommendation.get(index);
                    String reason = generatedPlaces.get(currentCandidate.getDestination());
                    return toLocationRecommendResponse(currentCandidate, index, minTime, reason);
                })
                .toList();
    }

    private RecommendationResponse toLocationRecommendResponse(
            final Candidate candidate,
            final int index,
            final int minTime,
            final String reason
    ) {
        final Place targetPlace = candidate.getDestination();
        final int totalTime = candidate.calculateAverageTravelTime();

        final List<PlaceRecommendResponse> recommendedPlaces = toPlaceRecommendResponses(candidate.getRecommendedPlaces(), index + 1);
        final List<RouteResponse> routes = toRouteResponses(candidate.getRoutes());

        return new RecommendationResponse(
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

    private List<PlaceRecommendResponse> toPlaceRecommendResponses(
            final List<RecommendedPlace> places,
            final int rank
    ) {
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

    private List<RouteResponse> toRouteResponses(final Routes routes) {
        return routes.getRoutes().stream()
                .map(this::toRouteResponse)
                .toList();
    }

    private RouteResponse toRouteResponse(final Route route) {
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

    private PathResponse toPathResponse(final Path path, final int order) {
        return new PathResponse(
                order,
                path.getStart().getName(),
                path.getStart().getPoint().getX(),
                path.getStart().getPoint().getY(),
                path.getEnd().getName(),
                path.getEnd().getPoint().getX(),
                path.getEnd().getPoint().getY(),
                path.getSubwayLineName(),
                path.getTravelTime().toMinutesPart()
        );
    }
}
