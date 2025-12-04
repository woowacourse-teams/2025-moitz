package com.f12.moitz.domain.subway;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class SubwayEdges {

    private final Set<SubwayEdge> subwayEdges;

    public static SubwayEdges of(final Map<SubwayStation, List<Edge>> subwayEdges) {
        final Set<SubwayEdge> subwayEdgeSet = subwayEdges.entrySet().stream()
                .map(entry -> {
                    final SubwayStation station = entry.getKey();
                    final SubwayEdge subwayEdge = new SubwayEdge(station);
                    entry.getValue().forEach(subwayEdge::addEdge);
                    return subwayEdge;
                })
                .collect(Collectors.toSet());
        return new SubwayEdges(subwayEdgeSet);
    }

    public StationSequence findShortestTimePath(final SubwayStation start, final SubwayStation end) {
        final SubwayPathFinder pathFinder = new SubwayPathFinder();
        return pathFinder.findShortestTimePath(this, start, end);
    }

    public Set<Edge> getEdges(final SubwayStation currentStation) {
        return subwayEdges.stream()
                .filter(edgeSet -> edgeSet.isSameStation(currentStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("현재 역에 해당하는 SubwayEdge가 존재하지 않습니다. 역 이름: " + currentStation.getName()))
                .getEdges();
    }

    public boolean isContainsStation(final SubwayStation station) {
        return subwayEdges.stream()
                .anyMatch(edgeSet -> edgeSet.isSameStation(station));
    }

    public Set<SubwayStation> getAllStations() {
        final Set<SubwayStation> stations = new HashSet<>();
        for (SubwayEdge edgeSet : subwayEdges) {
            stations.add(edgeSet.getSubwayStation());
        }
        return stations;
    }

    public SubwayStation getStationByName(final String stationName) {
        return subwayEdges.stream()
                .map(SubwayEdge::getSubwayStation)
                .filter(station -> station.isNameMatch(stationName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("이름이 일치하는 SubwayStation이 존재하지 않습니다: " + stationName));
    }

    public void addEdge(final SubwayStation subwayStation, final Edge edge) {
        final Optional<SubwayEdge> existingEdgeSet = subwayEdges.stream()
                .filter(edgeSet -> edgeSet.isSameStation(subwayStation))
                .findFirst();

        if (existingEdgeSet.isPresent()) {
            existingEdgeSet.get().addEdge(edge);
            return;
        }

        final SubwayEdge newEdgeSet = new SubwayEdge(subwayStation);
        newEdgeSet.addEdge(edge);
        subwayEdges.add(newEdgeSet);
    }

    public Optional<Edge> findEdgeBy(final SubwayStation from, final SubwayStation to, final SubwayLine line) {
        return subwayEdges.stream()
                .flatMap(subwayEdge -> subwayEdge.findEdgeBy(from, to, line).stream())
                .findFirst();
    }

}
