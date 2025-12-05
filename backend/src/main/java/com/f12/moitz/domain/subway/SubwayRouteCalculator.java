package com.f12.moitz.domain.subway;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.SubwayRouteException;
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

    public SubwayRouteCalculator(final SubwayEdges edges) {
        this.edges = edges;
    }

    public StationSequence findShortestTimePath(final SubwayStation start, final SubwayStation end) {
        try {
            validateStationsExist(start, end);
            validateNotSameStation(start, end);

            final Map<SubwayStation, List<PreviousInfo>> prev = searchShortestTimePath(start, end);

            return reconstructPaths(prev, start, end);
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

    private Map<SubwayStation, List<PreviousInfo>> searchShortestTimePath(
            final SubwayStation start,
            final SubwayStation end
    ) {
        final Map<SubwayStation, Integer> times = new HashMap<>();
        final Map<SubwayStation, List<PreviousInfo>> prev = new HashMap<>();
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
                    final List<PreviousInfo> previousInfos = prev.get(currentStation);
                    boolean isContinuous = previousInfos.stream()
                            .anyMatch(info -> info.isSameLine(currentLine));

                    // 환승 시간 추가: 현재 역에 도달할 수 있는 호선들 중 간선의 호선이 포함되어 있지 않은 경우
                    if (!isContinuous) {
                        Optional<Edge> transferEdge = currentEdges.stream()
                                .filter(currentEdge -> currentEdge.hasSameValue(currentStation, currentLine))
                                .findFirst();

                        if (transferEdge.isEmpty()) {
                            log.warn(
                                    "환승 Edge가 존재하지 않아 경로를 건너뜁니다. 현재역: {}, 다음역: {}, 환승호선: {} -> {} "
                                    + "(탐색경로 - 출발역: {}, 도착역: {})",
                                    currentStation.getName(),
                                    neighbor.getName(),
                                    previousInfos.getFirst().line.getTitle(),
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
                    prev.put(neighbor, new ArrayList<>(List.of(new PreviousInfo(currentStation, currentLine))));
                    pq.add(new Node(neighbor, newTime));
                } else if (newTime == neighborTime) {
                    final List<PreviousInfo> previousInfos = prev.get(neighbor);
                    previousInfos.add(new PreviousInfo(currentStation, currentLine));
                }
            }
        }
        return prev;
    }

    private StationSequence reconstructPaths(
            final Map<SubwayStation, List<PreviousInfo>> prev,
            final SubwayStation start,
            final SubwayStation end
    ) {
        final List<StationSegment> fullSegments = new ArrayList<>();
        SubwayStation current = end;
        SubwayLine preferredLine = null; // 다음에 탈 호선 (환승 최소화용)

        fullSegments.addFirst(new StationSegment(current, null));

        while (!start.equals(current)) {
            final List<PreviousInfo> previousInfos = prev.get(current);
            if (previousInfos == null || previousInfos.isEmpty()) {
                throw new SubwayRouteException(
                        ExternalApiErrorCode.SUBWAY_ROUTE_CALCULATION_FAILED,
                        "경로가 출발역까지 이어지지 않습니다."
                );
            }

            // 최적의 이전 역 선택 (환승 최소화)
            PreviousInfo selected = null;
            boolean needsTransfer = (preferredLine != null);

            if (preferredLine != null) {
                for (PreviousInfo candidate : previousInfos) {
                    // 1순위: 다음 경로와 같은 호선 (환승 없음)
                    if (candidate.isSameLine(preferredLine)) {
                        selected = candidate;
                        needsTransfer = false;
                        break;
                    }
                }
            }

            if (selected == null) {
                for (PreviousInfo candidate : previousInfos) {
                    // 2순위: 출발역
                    if (start.equals(candidate.station)) {
                        selected = candidate;
                        break;
                    }
                    // 3순위: 이전 역에서 연속성 있는 호선
                    if (candidate.canContinueFromPrevious(prev)) {
                        selected = candidate;
                        break;
                    }
                }
            }

            // 기본값
            if (selected == null) {
                selected = previousInfos.getFirst();
            }

            final SubwayStation currentStation = current;
            final SubwayStation previousStation = selected.station;
            final SubwayLine selectedLine = selected.line;

            if (needsTransfer) {
                final Edge transferEdge = getEdgeBy(currentStation, currentStation, preferredLine);
                fullSegments.addFirst(new StationSegment(currentStation, transferEdge));
            }

            final Edge movementEdge = getEdgeBy(previousStation, currentStation, selectedLine);
            fullSegments.addFirst(new StationSegment(previousStation, movementEdge));

            current = previousStation;
            preferredLine = selectedLine;
        }

        return new StationSequence(fullSegments);
    }

    private Edge getEdgeBy(final SubwayStation from, final SubwayStation to, final SubwayLine line) {
        return edges.findEdgeBy(from, to, line)
                .orElseThrow(() -> {
                    log.error("현재역: {}, 다음역: {}, 노선: {}", from.getName(), to.getName(), line.getTitle());
                    return new SubwayRouteException(
                            ExternalApiErrorCode.SUBWAY_ROUTE_CALCULATION_FAILED,
                            "다음 역으로 가는 Edge가 존재하지 않습니다."
                    );
                });
    }

    private record Node(SubwayStation station, int time) {

    }

    private record PreviousInfo(SubwayStation station, SubwayLine line) {

        private boolean isSameLine(final SubwayLine line) {
            return this.line == line;
        }

        private boolean canContinueFromPrevious(final Map<SubwayStation, List<PreviousInfo>> prev) {
            final List<PreviousInfo> beforeCurrent = prev.get(station);
            if (beforeCurrent == null || beforeCurrent.isEmpty()) {
                throw new SubwayRouteException(
                        ExternalApiErrorCode.SUBWAY_ROUTE_CALCULATION_FAILED,
                        "station이 출발역이 아니면 이전 역과 호선은 꼭 존재해야 합니다."
                );
            }

            return beforeCurrent.stream()
                    .anyMatch(info -> info.isSameLine(line));
        }

    }

}
