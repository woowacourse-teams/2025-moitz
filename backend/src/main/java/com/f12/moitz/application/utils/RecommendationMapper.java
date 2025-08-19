package com.f12.moitz.application.utils;

import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
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

    public RecommendationsResponse toResponse(
            final List<Place> startingPlaces,
            final Recommendation recommendation,
            final Map<Place, ReasonAndDescription> generatedPlaces
    ) {
        final int minTime = recommendation.getBestRecommendationTime();

        return new RecommendationsResponse(
                IntStream.range(0, startingPlaces.size())
                .mapToObj(index -> toStartingPlaceResponse(index, startingPlaces.get(index)))
                .toList(),
                IntStream.range(0, recommendation.size())
                .mapToObj(index -> {
                    Candidate currentCandidate = recommendation.get(index);
                    ReasonAndDescription reasonAndDescription = generatedPlaces.get(currentCandidate.getDestination());
                    return toLocationRecommendResponse(currentCandidate, index, minTime, reasonAndDescription);
                })
                .toList()
        );
    }

    public Result toResult(
            final List<Place> startingPlaces,
            final Recommendation recommendation,
            final Map<Place, String> generatedPlaces
    ) {
        final int minTime = recommendation.getBestRecommendationTime();

        return new Result(
                IntStream.range(0, startingPlaces.size())
                        .mapToObj(index -> toStartingPlaceResponse(index, startingPlaces.get(index)))
                        .toList(),
                IntStream.range(0, recommendation.size())
                        .mapToObj(index -> {
                            Candidate currentCandidate = recommendation.get(index);
                            String reason = generatedPlaces.get(currentCandidate.getDestination());
                            return toLocationRecommendResponse(currentCandidate, index, minTime, reason);
                        })
                        .toList()
        );
    }

    private StartingPlaceResponse toStartingPlaceResponse(final int index, final Place startingPlace) {
        return new StartingPlaceResponse(
                index + 1,
                index + 1,
                startingPlace.getPoint().getX(),
                startingPlace.getPoint().getY(),
                startingPlace.getName()
        );
    }

    private RecommendationResponse toLocationRecommendResponse(
            final Candidate candidate,
            final int index,
            final int minTime,
            final ReasonAndDescription reasonAndDescription
    ) {
        final Place targetPlace = candidate.getDestination();
        final int totalTime = candidate.calculateAverageTravelTime();

        final List<PlaceRecommendResponse> recommendedPlaces = toPlaceRecommendResponses(candidate.getRecommendedPlaces());
        final List<RouteResponse> routes = toRouteResponses(candidate.getRoutes());

        return new RecommendationResponse(
                (long) index + 1,
                index + 1,
                targetPlace.getPoint().getY(),
                targetPlace.getPoint().getX(),
                targetPlace.getName(),
                totalTime,
                totalTime == minTime,
                reasonAndDescription.description(),
                reasonAndDescription.reason(),
                recommendedPlaces,
                routes
        );
    }

    private List<PlaceRecommendResponse> toPlaceRecommendResponses(
            final List<RecommendedPlace> places
    ) {
        return IntStream.range(0, places.size())
                .mapToObj(i -> {
                    RecommendedPlace p = places.get(i);
                    return new PlaceRecommendResponse(
                            i + 1,
                            p.getPlaceName(),
                            p.getCategory(),
                            p.getWalkingTime(),
                            p.getUrl()
                    );
                })
                .toList();
    }

    private List<RouteResponse> toRouteResponses(final Routes routes) {
        return IntStream.range(0, routes.getRoutes().size())
                .mapToObj(index -> toRouteResponse(routes.getRoutes().get(index) ,index + 1))
                .toList();
    }

    private RouteResponse toRouteResponse(final Route route, final long id) {
        List<PathResponse> pathResponses = IntStream.range(0, route.getPaths().size())
                .mapToObj(pathIndex -> toPathResponse(route.getPaths().get(pathIndex), pathIndex + 1))
                .toList();

        return new RouteResponse(
                // TODO: 아이디로 변경
                id,
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
