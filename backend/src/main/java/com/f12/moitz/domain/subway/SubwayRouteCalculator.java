package com.f12.moitz.domain.subway;

import com.f12.moitz.common.error.exception.SubwayRouteException;
import com.f12.moitz.domain.subway.SubwayRouteSearchResult.PreviousStation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubwayRouteCalculator {

    private static final int UNREACHABLE_TIME = Integer.MAX_VALUE;

    private final SubwayEdges edges;
    private final StationSequenceReconstructor stationSequenceReconstructor;

    public SubwayRouteCalculator(final SubwayEdges edges) {
        this.edges = edges;
        this.stationSequenceReconstructor = new StationSequenceReconstructor(edges);
    }

    public StationSequence findShortestTimePath(final SubwayStation start, final SubwayStation end) {
        try {
            validateStationsExist(start, end);
            validateNotSameStation(start, end);

            final SubwayRouteSearchResult searchResult = searchShortestTimePath(start, end);

            return stationSequenceReconstructor.reconstruct(searchResult, start, end);
        } catch (SubwayRouteException e) {
            log.error("지하철 경로 탐색 실패. 출발역: {}, 도착역: {}", start.getName(), end.getName());
            throw e;
        }
    }

    private void validateStationsExist(final SubwayStation start, final SubwayStation end) {
        if (!edges.containsStation(start) || !edges.containsStation(end)) {
            log.error("노선도에 존재하지 않는 역입니다. 출발역: {}, 도착역: {}", start.getName(), end.getName());
            throw new IllegalStateException("출발역 또는 도착역이 노선도에 존재하지 않아 경로를 찾을 수 없습니다.");
        }
    }

    private void validateNotSameStation(final SubwayStation start, final SubwayStation end) {
        if (start.equals(end)) {
            log.error("동일한 출발역, 도착역: {}", start.getName());
            throw new IllegalStateException("출발역과 도착역은 동일할 수 없습니다.");
        }
    }

    private SubwayRouteSearchResult searchShortestTimePath(
            final SubwayStation start,
            final SubwayStation end
    ) {
        final Map<SubwayStation, Integer> times = new HashMap<>();
        final Map<SubwayStation, List<PreviousStation>> prev = new HashMap<>();
        final PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.time));
        final Set<SubwayStation> visited = new HashSet<>();

        times.put(start, 0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            final Node current = pq.poll();
            // 현재 역 정보 가져오기
            final SubwayStation currentStation = current.station;

            if (!edges.containsStation(currentStation)) {
                continue;
            }
            if (!visited.add(currentStation)) {
                continue;
            }
            if (end.equals(currentStation)) {
                break;
            }

            final Set<Edge> currentEdges = edges.getEdges(currentStation);

            for (Edge edge : currentEdges) {
                final SubwayStation neighbor = edge.getDestination();

                if (visited.contains(neighbor)) {
                    continue;
                }

                final SubwayLine currentLine = edge.getSubwayLine();
                int newTime = times.getOrDefault(currentStation, UNREACHABLE_TIME) + edge.getTimeInSeconds();

                if (!start.equals(currentStation)) {
                    final List<PreviousStation> previousStations = prev.get(currentStation);
                    boolean isContinuous = previousStations.stream()
                            .anyMatch(info -> info.isSameLine(currentLine));

                    // 환승 시간 추가: 현재 역에 도달할 수 있는 호선들 중 간선의 호선이 포함되어 있지 않은 경우
                    if (!isContinuous) {
                        Optional<Edge> transferEdge = currentEdges.stream()
                                .filter(currentEdge -> currentEdge.hasSameValue(currentStation, currentLine))
                                .findFirst();

                        if (transferEdge.isEmpty()) {
                            log.warn(
                                    """
                                    환승 Edge가 존재하지 않아 경로를 건너뜁니다. 현재역: {}, 다음역: {}, 환승호선: {} -> {}
                                    (탐색경로 - 출발역: {}, 도착역: {})
                                    """,
                                    currentStation.getName(),
                                    neighbor.getName(),
                                    previousStations.getFirst().line().getTitle(),
                                    currentLine.getTitle(),
                                    start,
                                    end
                            );
                            continue;
                        }

                        newTime += transferEdge.get().getTimeInSeconds();
                    }
                }

                int neighborTime = times.getOrDefault(neighbor, UNREACHABLE_TIME);

                if (newTime < neighborTime) {
                    times.put(neighbor, newTime);
                    prev.put(neighbor, new ArrayList<>(List.of(new PreviousStation(currentStation, currentLine))));
                    pq.add(new Node(neighbor, newTime));
                } else if (newTime == neighborTime) {
                    final List<PreviousStation> previousStations = prev.get(neighbor);
                    previousStations.add(new PreviousStation(currentStation, currentLine));
                }
            }
        }
        return new SubwayRouteSearchResult(prev);
    }

    private record Node(SubwayStation station, int time) {

    }

}
