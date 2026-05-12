package com.f12.moitz.application.subway.setup;

import com.f12.moitz.application.port.subway.dto.RawPathInfo;
import com.f12.moitz.application.port.subway.dto.RawRouteInfo;
import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayStation;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Component;

@Component
public class SubwayEdgesBuilder {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public SubwayEdges build(final List<SubwayStation> stations, final List<RawRouteInfo> rawRoutes) {
        final Map<SubwayStation, List<Edge>> stationMap = new HashMap<>();
        for (RawRouteInfo rawRoute : rawRoutes) {
            addRouteEdges(stations, stationMap, rawRoute);
        }
        return SubwayEdges.of(stationMap);
    }

    private void addRouteEdges(
            final List<SubwayStation> stations,
            final Map<SubwayStation, List<Edge>> stationMap,
            final RawRouteInfo rawRoute
    ) {
        final List<RawPathInfo> paths = rawRoute.paths();
        for (int i = 0; i < paths.size(); i++) {
            addPathEdges(stations, stationMap, paths, i);
        }
    }

    private void addPathEdges(
            final List<SubwayStation> stations,
            final Map<SubwayStation, List<Edge>> stationMap,
            final List<RawPathInfo> paths,
            final int pathIndex
    ) {
        final RawPathInfo currentPath = paths.get(pathIndex);
        final String fromName = currentPath.departureStation().stationName();
        final String toName = currentPath.arrivalStation().stationName();
        final String fromLine = currentPath.departureStation().lineName();
        final String toLine = currentPath.arrivalStation().lineName();

        final SubwayStation fromStation = getStationByName(stations, fromName);
        final SubwayStation toStation = getStationByName(stations, toName);

        final int distance = currentPath.stationSectionDistance();
        final int travelTimeInSeconds = calculateTravelTime(currentPath, paths, pathIndex);

        final List<Edge> fromEdges = stationMap.computeIfAbsent(fromStation, key -> new ArrayList<>());
        final List<Edge> toEdges = stationMap.computeIfAbsent(toStation, key -> new ArrayList<>());

        fromEdges.add(new Edge(toStation, travelTimeInSeconds, distance, fromLine));
        if (currentPath.isTransfer()) {
            fromEdges.add(new Edge(toStation, travelTimeInSeconds, distance, toLine));
            return;
        }
        toEdges.add(new Edge(fromStation, travelTimeInSeconds, distance, fromLine));
    }

    private SubwayStation getStationByName(final List<SubwayStation> stations, final String stationName) {
        return stations.stream()
                .filter(station -> station.getName().equals(stationName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("이름이 일치하는 SubwayStation이 존재하지 않습니다: " + stationName));
    }

    private int calculateTravelTime(
            final RawPathInfo currentPath,
            final List<RawPathInfo> allPaths,
            final int currentIndex
    ) {
        if (currentPath.isTransfer()) {
            return currentPath.waitingSeconds() + currentPath.requiredSeconds();
        }

        if (currentIndex < allPaths.size() - 1) {
            final RawPathInfo nextPath = allPaths.get(currentIndex + 1);
            final String currentDepartureTime = currentPath.trainDepartureTime();
            final String nextDepartureTime = nextPath.trainDepartureTime();

            if (currentDepartureTime != null && nextDepartureTime != null) {
                return calculateTimeDifference(currentDepartureTime, nextDepartureTime);
            }
        }
        return currentPath.requiredSeconds();
    }

    private int calculateTimeDifference(final String currentDepartureTime, final String nextDepartureTime) {
        try {
            final LocalTime current = LocalTime.parse(currentDepartureTime, TIME_FORMATTER);
            LocalTime next = LocalTime.parse(nextDepartureTime, TIME_FORMATTER);
            if (next.isBefore(current)) {
                next = next.plusHours(24);
            }
            return (int) Duration.between(current, next).getSeconds();
        } catch (DateTimeException | ArithmeticException e) {
            throw new IllegalStateException("경로의 출발 시간을 파싱할 수 없습니다.");
        }
    }

}
