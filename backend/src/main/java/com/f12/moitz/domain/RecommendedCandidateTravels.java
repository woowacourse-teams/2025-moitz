package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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

    public Routes getRoutes(final Place place) {
        final Routes routes = routesByPlace.get(place);
        if (routes == null) {
            throw new IllegalArgumentException("추천 후보 경로가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return routes;
    }

    public Courses getCourses(final Place place) {
        final Courses courses = coursesByPlace.get(place);
        if (courses == null) {
            throw new IllegalArgumentException("추천 후보 이동 코스가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return courses;
    }

}
