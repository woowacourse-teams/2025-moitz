package com.f12.moitz.domain;

import java.util.List;
import java.util.stream.IntStream;

public class RouteOrigins {

    private final List<Place> origins;

    public RouteOrigins(final List<? extends Place> origins) {
        validate(origins);
        this.origins = List.copyOf(origins);
    }

    private void validate(final List<? extends Place> origins) {
        if (origins == null || origins.isEmpty()) {
            throw new IllegalArgumentException("경로 출발지는 비어있을 수 없습니다.");
        }
        if (origins.stream().anyMatch(origin -> origin == null)) {
            throw new IllegalArgumentException("경로 출발지는 null일 수 없습니다.");
        }
        final long distinctCount = origins.stream()
                .distinct()
                .count();
        if (distinctCount != origins.size()) {
            throw new IllegalArgumentException("경로 출발지는 중복될 수 없습니다.");
        }
    }

    public List<OriginDestination> createOriginDestinationsTo(final List<? extends Place> destinations) {
        validateDestinations(destinations);
        return destinations.stream()
                .flatMap(destination -> origins.stream()
                        .map(origin -> new OriginDestination(origin, destination)))
                .toList();
    }

    private void validateDestinations(final List<? extends Place> destinations) {
        if (destinations == null) {
            throw new IllegalArgumentException("경로 도착지는 null일 수 없습니다.");
        }
        if (destinations.stream().anyMatch(destination -> destination == null)) {
            throw new IllegalArgumentException("경로 도착지는 null일 수 없습니다.");
        }
    }

    public List<OriginDestination> createOriginDestinationsBetweenOrigins() {
        return IntStream.range(0, origins.size())
                .boxed()
                .flatMap(left -> IntStream.range(left + 1, origins.size())
                        .mapToObj(right -> new OriginDestination(origins.get(left), origins.get(right))))
                .toList();
    }

    public int size() {
        return origins.size();
    }

    public List<String> getNames() {
        return origins.stream()
                .map(Place::getName)
                .toList();
    }

    public List<Place> getOrigins() {
        return origins;
    }

}
