package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SelectedCandidateRouteService {

    private final RouteFinder routeFinder;

    public SelectedCandidateRouteService(
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.routeFinder = routeFinder;
    }

    public SelectedCandidateRouteResult prepare(
            final RouteOrigins routeOrigins,
            final List<Place> finalPlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsTo(finalPlaces);
        return new SelectedCandidateRouteResult(
                collectRoutes(finalPlaces, candidateRoutes),
                findCoursesByDestination(originDestinations)
        );
    }

    private Map<Place, Routes> collectRoutes(
            final List<Place> finalPlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return finalPlaces.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        candidateRoutes::get
                ));
    }

    private Map<Place, Courses> findCoursesByDestination(final List<OriginDestination> originDestinations) {
        final List<Course> courses = routeFinder.findCourses(originDestinations);
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

}
