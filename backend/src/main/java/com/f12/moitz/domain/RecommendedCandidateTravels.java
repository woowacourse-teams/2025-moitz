package com.f12.moitz.domain;

import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.Courses;
import com.f12.moitz.domain.route.Routes;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class RecommendedCandidateTravels {

    private final Map<Place, Routes> routesByPlace;
    private final Map<Place, Courses> coursesByPlace;

    public RecommendedCandidateTravels(
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace
    ) {
        validate(routesByPlace, coursesByPlace);
        this.routesByPlace = Collections.unmodifiableMap(new LinkedHashMap<>(routesByPlace));
        this.coursesByPlace = Collections.unmodifiableMap(new LinkedHashMap<>(coursesByPlace));
    }

    public RecommendedCandidateTravels(final Map<Place, List<CandidateRoute>> candidateRoutesByPlace) {
        validate(candidateRoutesByPlace);
        this.routesByPlace = Collections.unmodifiableMap(toRoutesByPlace(candidateRoutesByPlace));
        this.coursesByPlace = Collections.unmodifiableMap(toCoursesByPlace(candidateRoutesByPlace));
    }

    private void validate(final Map<Place, List<CandidateRoute>> candidateRoutesByPlace) {
        if (candidateRoutesByPlace == null) {
            throw new IllegalArgumentException("추천 후보 경로는 null일 수 없습니다.");
        }
        if (candidateRoutesByPlace.keySet().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 후보 장소는 null일 수 없습니다.");
        }
        candidateRoutesByPlace.forEach(this::validateCandidateRoutes);
    }

    private void validateCandidateRoutes(final Place place, final List<CandidateRoute> candidateRoutes) {
        if (candidateRoutes == null || candidateRoutes.isEmpty()) {
            throw new IllegalArgumentException("후보 경로 목록은 비어있거나 null일 수 없습니다. 추천 지역: " + place.getName());
        }
        if (candidateRoutes.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("후보 경로 목록에 null이 포함될 수 없습니다. 추천 지역: " + place.getName());
        }
    }

    private Map<Place, Routes> toRoutesByPlace(final Map<Place, List<CandidateRoute>> candidateRoutesByPlace) {
        final Map<Place, Routes> routesByPlace = new LinkedHashMap<>();
        candidateRoutesByPlace.forEach((place, candidateRoutes) -> routesByPlace.put(
                place,
                new Routes(candidateRoutes.stream()
                        .map(CandidateRoute::getRoute)
                        .toList())
        ));
        return routesByPlace;
    }

    private Map<Place, Courses> toCoursesByPlace(final Map<Place, List<CandidateRoute>> candidateRoutesByPlace) {
        final Map<Place, Courses> coursesByPlace = new LinkedHashMap<>();
        candidateRoutesByPlace.forEach((place, candidateRoutes) -> coursesByPlace.put(
                place,
                new Courses(candidateRoutes.stream()
                        .map(CandidateRoute::getCourse)
                        .toList())
        ));
        return coursesByPlace;
    }

    private void validate(
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace
    ) {
        if (routesByPlace == null) {
            throw new IllegalArgumentException("추천 후보 경로는 null일 수 없습니다.");
        }
        if (coursesByPlace == null) {
            throw new IllegalArgumentException("추천 후보 이동 코스는 null일 수 없습니다.");
        }
        if (!routesByPlace.keySet().equals(coursesByPlace.keySet())) {
            throw new IllegalArgumentException("추천 후보 경로와 이동 코스의 장소 목록은 같아야 합니다.");
        }
        if (routesByPlace.keySet().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 후보 장소는 null일 수 없습니다.");
        }
        routesByPlace.forEach((place, routes) -> validateTravel(place, routes, coursesByPlace.get(place)));
    }

    private void validateTravel(final Place place, final Routes routes, final Courses courses) {
        if (routes == null) {
            throw new IllegalArgumentException("추천 후보 경로는 null일 수 없습니다. 추천 지역: " + place.getName());
        }
        if (courses == null) {
            throw new IllegalArgumentException("추천 후보 이동 코스는 null일 수 없습니다. 추천 지역: " + place.getName());
        }
        if (routes.size() != courses.size()) {
            throw new IllegalArgumentException("추천 후보 경로와 이동 코스의 개수는 같아야 합니다. 추천 지역: " + place.getName());
        }
    }

    public List<CandidateRoute> getCandidateRoutes(final Place place) {
        final Routes routes = getRoutes(place);
        final Courses courses = getCourses(place);
        return IntStream.range(0, routes.size())
                .mapToObj(index -> new CandidateRoute(routes.get(index), courses.get(index)))
                .toList();
    }

    private Routes getRoutes(final Place place) {
        validatePlace(place);
        final Routes routes = routesByPlace.get(place);
        if (routes == null) {
            throw new IllegalArgumentException("추천 후보 경로가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return routes;
    }

    private Courses getCourses(final Place place) {
        validatePlace(place);
        final Courses courses = coursesByPlace.get(place);
        if (courses == null) {
            throw new IllegalArgumentException("추천 후보 이동 코스가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return courses;
    }

    private void validatePlace(final Place place) {
        if (place == null) {
            throw new IllegalArgumentException("추천 후보 장소는 null일 수 없습니다.");
        }
    }

}
