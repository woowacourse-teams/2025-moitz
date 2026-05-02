package com.f12.moitz.application.utils;

import com.f12.moitz.application.dto.LocationResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.PointResponse;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    private static final boolean BEST_RECOMMENDATION_DISABLED = false;

    public RecommendationResultResponse toResponse(final Result result) {
        final List<String> condition = getCondition(result);
        return new RecommendationResultResponse(
                condition,
                IntStream.range(0, result.getStartingPlacesCount())
                        .mapToObj(index -> toStartingPlaceResponse(index, result.getStartingPlaces().get(index)))
                        .toList(),
                IntStream.range(0, result.getRecommendedLocationsCount())
                        .mapToObj(index -> {
                            Candidate currentCandidate = result.getRecommendedLocations().get(index);
                            return toLocationRecommendResponse(currentCandidate, index);
                        })
                        .toList()
        );
    }

    private List<String> getCondition(final Result result) {
        final List<RecommendCondition> recommendConditions = result.getRecommendConditions();

        return recommendConditions.stream()
                .map(RecommendCondition::getTitle)
                .toList();
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

    private LocationResponse toLocationRecommendResponse(
            final Candidate candidate,
            final int index
    ) {
        final Place targetPlace = candidate.getDestination();
        final int totalTime = candidate.calculateAverageTravelTime();

        final Map<RecommendCondition, List<PlaceRecommendResponse>> recommendedPlaces = toPlaceRecommendResponses(
                candidate.getRecommendedPlaces()
        );
        final List<RouteResponse> routes = toRouteResponses(candidate.getRoutes(), candidate.getCourses());

        return new LocationResponse(
                (long) index + 1,
                index + 1,
                targetPlace.getPoint().getY(),
                targetPlace.getPoint().getX(),
                targetPlace.getName(),
                totalTime,
                // 추천 방식 변경으로 기존 평균 이동시간 기준 best 표시는 임시 비활성화한다.
                BEST_RECOMMENDATION_DISABLED,
                candidate.getTags().stream()
                        .map(CandidateSelectionTag::name)
                        .toList(),
                candidate.getDescription(),
                candidate.getReason(),
                recommendedPlaces,
                routes
        );
    }

    private Map<RecommendCondition, List<PlaceRecommendResponse>> toPlaceRecommendResponses(
            final CategorizedRecommendedPlaces categorizedRecommendedPlaces
    ) {
        return categorizedRecommendedPlaces.getCategorizedPlaces().entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> IntStream.range(0, entry.getValue().size())
                                .mapToObj(i -> {
                                    RecommendedPlace p = entry.getValue().get(i);
                                    return new PlaceRecommendResponse(
                                            i + 1,       // 순번
                                            p.getX(),
                                            p.getY(),
                                            p.getName(),
                                            p.getCategory(),
                                            p.getWalkingTime(),
                                            p.getPlaceUrl(),
                                            p.getImageUrl()
                                    );
                                })
                                .toList()
                ));
    }

    private List<RouteResponse> toRouteResponses(final Routes routes, final Courses courses) {
        return IntStream.range(0, routes.getRoutes().size())
                .mapToObj(index -> toRouteResponse(
                        routes.getRoutes().get(index),
                        courses.getCourses().get(index),
                        index + 1
                )).toList();
    }

    private RouteResponse toRouteResponse(final Route route, final Course course, final long id) {
        final List<PathResponse> pathResponses = IntStream.range(0, route.getPaths().size())
                .mapToObj(pathIndex -> toPathResponse(route.getPaths().get(pathIndex), pathIndex + 1))
                .toList();

        final List<PointResponse> pointResponses = IntStream.range(0, course.size())
                .mapToObj(pointIndex -> toPointResponse(course.getPoints().get(pointIndex), pointIndex + 1))
                .toList();

        return new RouteResponse(
                // TODO: 아이디로 변경
                id,
                route.calculateTransferCount(),
                route.calculateTotalTravelTime(),
                pathResponses,
                pointResponses
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
                path.getSubwayLine() != null ? path.getSubwayLine().getTitle() : null,
                (int) path.getTravelTime().toMinutes()
        );
    }

    private PointResponse toPointResponse(final Point point, final int order) {
        return new PointResponse(
                order,
                point.getX(),
                point.getY()
        );
    }

}
