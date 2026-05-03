package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RecommendedCandidateRouteService {

    private final RouteFinder routeFinder;

    public RecommendedCandidateRouteService(
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.routeFinder = routeFinder;
    }

    public RecommendedCandidateTravels prepare(
            final RouteOrigins routeOrigins,
            final RecommendedCandidates recommendedCandidates,
            final Map<Place, Routes> candidateRoutes
    ) {
        final List<Place> recommendedCandidatePlaces = recommendedCandidates.getPlaces();
        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsTo(recommendedCandidatePlaces);
        return new RecommendedCandidateTravels(
                collectRoutes(recommendedCandidatePlaces, candidateRoutes),
                findCoursesByDestination(originDestinations)
        );
    }

    private Map<Place, Routes> collectRoutes(
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        final Map<Place, Routes> routesByPlace = new LinkedHashMap<>();
        recommendedCandidatePlaces.forEach(place -> routesByPlace.put(place, candidateRoutes.get(place)));
        return routesByPlace;
    }

    private Map<Place, Courses> findCoursesByDestination(final List<OriginDestination> originDestinations) {
        final List<Course> courses = routeFinder.findCourses(originDestinations);
        validateCourseCount(originDestinations, courses);
        return IntStream.range(0, originDestinations.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        index -> originDestinations.get(index).getDestination(),
                        Collectors.mapping(
                                courses::get,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new Courses(entry.getValue())
                ));
    }

    private void validateCourseCount(
            final List<OriginDestination> originDestinations,
            final List<Course> courses
    ) {
        if (courses == null) {
            throw new IllegalStateException("추천 후보 이동 코스 조회 결과가 null입니다.");
        }
        if (originDestinations.size() != courses.size()) {
            throw new IllegalStateException(String.format(
                    "추천 후보 이동 코스 조회 결과 개수가 일치하지 않습니다. 요청=%d, 응답=%d",
                    originDestinations.size(),
                    courses.size()
            ));
        }
    }

}
