package com.f12.moitz.domain.route;

import com.f12.moitz.domain.place.Place;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OriginDestinations {

    private final List<OriginDestination> values;

    public OriginDestinations(final List<OriginDestination> values) {
        validate(values);
        this.values = List.copyOf(values);
    }

    private void validate(final List<OriginDestination> values) {
        if (values == null) {
            throw new IllegalArgumentException("출발도착 목록은 null일 수 없습니다.");
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("출발도착 목록에 null이 포함될 수 없습니다.");
        }
    }

    public Map<Place, Routes> groupRoutesByDestination(final List<Route> routes) {
        return toRoutesByDestination(groupByDestination(routes, "후보 경로 조회 결과"));
    }

    public Map<Place, List<CandidateRoute>> groupCandidateRoutesByDestination(
            final List<CandidateRoute> candidateRoutes
    ) {
        return groupByDestination(candidateRoutes, "추천 후보 경로 조회 결과");
    }

    private <T> Map<Place, List<T>> groupByDestination(final List<T> results, final String resultName) {
        validateResultCount(results, resultName);

        final Map<Place, List<T>> resultsByDestination = new LinkedHashMap<>();
        for (int index = 0; index < values.size(); index++) {
            final Place destination = values.get(index).getDestination();
            resultsByDestination.computeIfAbsent(destination, ignored -> new ArrayList<>())
                    .add(results.get(index));
        }
        return resultsByDestination;
    }

    private void validateResultCount(final List<?> results, final String resultName) {
        if (results == null) {
            throw new IllegalStateException(resultName + "가 null입니다.");
        }
        if (values.size() != results.size()) {
            throw new IllegalStateException(String.format(
                    "%s 개수가 일치하지 않습니다. 요청=%d, 응답=%d",
                    resultName,
                    values.size(),
                    results.size()
            ));
        }
        if (results.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException(resultName + "에 null이 포함될 수 없습니다.");
        }
    }

    private Map<Place, Routes> toRoutesByDestination(final Map<Place, List<Route>> routesByDestination) {
        final Map<Place, Routes> routes = new LinkedHashMap<>();
        routesByDestination.forEach((destination, destinationRoutes) -> routes.put(
                destination,
                new Routes(destinationRoutes)
        ));
        return routes;
    }

    public List<OriginDestination> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

}
