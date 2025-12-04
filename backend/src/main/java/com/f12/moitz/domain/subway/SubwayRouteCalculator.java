package com.f12.moitz.domain.subway;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubwayRouteCalculator {

    private final SubwayEdges edges;

    public SubwayRouteCalculator(final SubwayEdges edges) {
        this.edges = edges;
    }

    public StationSequence findShortestTimePath(final SubwayStation start, final SubwayStation end) {
        if (!edges.isContainsStation(start) || !edges.isContainsStation(end)) {
            log.error("노선도에 존재하지 않는 역입니다. 출발역: {}, 도착역: {}", start.getName(), end.getName());
            throw new IllegalStateException("출발역 또는 도착역이 노선도에 존재하지 않아 경로를 찾을 수 없습니다.");
        }
        if (start.equals(end)) {
            log.error("동일한 출발역, 도착역: {}", start.getName());
            throw new IllegalStateException("출발역과 도착역은 동일할 수 없습니다.");
        }

        final Map<SubwayStation, Integer> times = new HashMap<>();
        final Map<SubwayStation, List<PreviousInfo>> prev = new HashMap<>();
        final PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.time));
        final Set<SubwayStation> visited = new HashSet<>();

        // 초기화
        for (SubwayStation station : edges.getAllStations()) {
            times.put(station, Integer.MAX_VALUE);
        }
        times.put(start, 0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            final Node current = pq.poll();
            // 현재 역 정보 가져오기
            final SubwayStation currentStation = current.station;

            if (!edges.isContainsStation(currentStation)) {
                continue;
            }
            if (!visited.add(currentStation)) {
                continue;
            }
            if (end.equals(currentStation)) {
                break;
            }

            Set<Edge> currentEdges = edges.getEdges(currentStation);

            for (Edge edge : currentEdges) {
                final SubwayStation neighbor = edge.getDestination();

                if (visited.contains(neighbor)) {
                    continue;
                }
                if (times.get(currentStation) == null || times.get(neighbor) == null) {
                    log.warn("출발역({}) 혹은 도착역({})에 도달하는 시간이 초기화되지 않았습니다.", currentStation.getName(), neighbor.getName());
                    continue;
                }

                final SubwayLine currentLine = edge.getSubwayLine();
                int newTime = times.get(currentStation) + edge.getTimeInSeconds();

                if (!start.equals(currentStation)) {
                    final List<SubwayLine> previousLines = getPreviousLines(prev.get(currentStation));

                    // 환승 시간 추가: 현재 역에 도달할 수 있는 호선들 중 간선의 호선이 포함되어 있지 않은 경우
                    if (!previousLines.contains(currentLine)) {
                        Edge transferEdge = null;
                        for (Edge currentEdge : currentEdges) {
                            if (currentEdge.hasSameValue(currentStation, currentLine)) {
                                transferEdge = currentEdge;
                                break;
                            }
                        }
                        if (transferEdge == null) {
                            log.warn(
                                    "환승 Edge가 존재하지 않아 경로를 건너뜁니다. 현재역: {}, 다음역: {}, 환승호선: {} -> {}",
                                    currentStation.getName(),
                                    neighbor.getName(),
                                    previousLines.getFirst().getTitle(),
                                    currentLine.getTitle()
                            );
                            continue;
                        }

                        newTime += transferEdge.getTimeInSeconds();
                    }
                }

                if (newTime < times.get(neighbor)) {
                    times.put(neighbor, newTime);
                    prev.put(neighbor, new ArrayList<>(List.of(new PreviousInfo(currentStation, currentLine))));
                    pq.add(new Node(neighbor, newTime));
                } else if (newTime == times.get(neighbor)) {
                    final List<PreviousInfo> previousInfos = prev.get(neighbor);
                    previousInfos.add(new PreviousInfo(currentStation, currentLine));
                }
            }
        }

        return reconstructPaths(prev, start, end);
    }

    private List<SubwayLine> getPreviousLines(final List<PreviousInfo> previousInfos) {
        return previousInfos.stream()
                .map(p -> p.line)
                .toList();
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
            List<PreviousInfo> previousInfos = prev.get(current);
            if (previousInfos == null || previousInfos.isEmpty()) {
                throw new IllegalStateException("경로가 출발역까지 이어지지 않습니다.");
            }

            // 최적의 이전 역 선택 (환승 최소화)
            PreviousInfo selected = null;
            boolean needsTransfer = false;

            if (preferredLine != null) {
                for (PreviousInfo candidate : previousInfos) {
                    // 1순위: 다음 경로와 같은 호선 (환승 없음)
                    if (candidate.isSameLine(preferredLine)) {
                        selected = candidate;
                        break;
                    }
                }
            }
            if (selected == null) {
                for (PreviousInfo candidate : previousInfos) {
                    // 2순위: 출발역
                    if (start.equals(candidate.station)) {
                        selected = candidate;
                        if (preferredLine != null) {
                            needsTransfer = true;
                        }
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
                needsTransfer = true;
            }

            final SubwayStation currentStation = current;
            final SubwayStation previousStation = selected.station;
            final SubwayLine selectedLine = selected.line;

            // 환승이 필요하면 환승 Edge 먼저 추가
            if (needsTransfer) {
                Edge transferEdge = edges.findEdgeBy(previousStation, previousStation, selectedLine)
                        .orElseThrow(() -> {
                            log.error("현재역: {}, 환승호선: {}", previousStation.getName(), selectedLine.getTitle());
                            return new IllegalStateException("환승역이지만 환승 Edge가 존재하지 않습니다.");
                        });
                fullSegments.addFirst(new StationSegment(previousStation, transferEdge));
            }

            // 이동 Edge 추가
            Edge movementEdge = edges.findEdgeBy(previousStation, currentStation, selectedLine)
                    .orElseThrow(() -> {
                        log.error("현재역: {}, 다음역: {}, 노선: {}", previousStation.getName(), currentStation.getName(), selectedLine.getTitle());
                        return new IllegalStateException("다음 역으로 가는 Edge가 존재하지 않습니다.");
                    });
            fullSegments.addFirst(new StationSegment(previousStation, movementEdge));

            current = previousStation;
            preferredLine = selectedLine;
        }

        return new StationSequence(fullSegments);
    }

    private static class Node {
        SubwayStation station;
        int time;

        Node(final SubwayStation station, final int time) {
            this.station = station;
            this.time = time;
        }
    }

    private static class PreviousInfo {
        SubwayStation station;
        SubwayLine line;

        PreviousInfo(final SubwayStation station, final SubwayLine line) {
            this.station = station;
            this.line = line;
        }

        private boolean isSameLine(final SubwayLine line) {
            return this.line == line;
        }

        private boolean canContinueFromPrevious(final Map<SubwayStation, List<PreviousInfo>> prev) {
            List<PreviousInfo> beforeCurrent = prev.get(station);
            if (beforeCurrent == null || beforeCurrent.isEmpty()) {
                throw new IllegalStateException("station이 출발역이 아니면 이전 역과 호선은 꼭 존재해야 합니다.");
            }
            return beforeCurrent.stream()
                    .anyMatch(info -> info.isSameLine(line));
        }
    }

}
