package com.f12.moitz.domain.subway;

import com.f12.moitz.common.error.exception.SubwayRouteException;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubwayRouteCalculator {

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
        final SubwayRouteSearchState searchState = new SubwayRouteSearchState(start);

        while (searchState.hasNext()) {
            final SubwayStation currentStation = searchState.pollStation();

            if (!edges.containsStation(currentStation)) {
                continue;
            }
            if (!searchState.visit(currentStation)) {
                continue;
            }
            if (end.equals(currentStation)) {
                break;
            }

            final Set<Edge> currentEdges = edges.getEdges(currentStation);

            for (Edge edge : currentEdges) {
                final SubwayStation neighbor = edge.getDestination();

                if (searchState.isVisited(neighbor)) {
                    continue;
                }

                final SubwayLine currentLine = edge.getSubwayLine();
                int newTime = searchState.calculateTimeTo(currentStation, edge);

                if (!start.equals(currentStation)) {
                    // 환승 시간 추가: 현재 역에 도달할 수 있는 호선들 중 간선의 호선이 포함되어 있지 않은 경우
                    if (!searchState.canContinueOn(currentStation, currentLine)) {
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
                                    searchState.getFirstPreviousLine(currentStation).getTitle(),
                                    currentLine.getTitle(),
                                    start,
                                    end
                            );
                            continue;
                        }

                        newTime += transferEdge.get().getTimeInSeconds();
                    }
                }

                searchState.recordIfShorter(currentStation, neighbor, currentLine, newTime);
            }
        }
        return searchState.toResult();
    }

}
