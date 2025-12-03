package com.f12.moitz.domain.subway;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
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
        if (!isContainsStation(start) || !isContainsStation(end)) {
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
        for (SubwayStation station : getAllStations()) {
            times.put(station, Integer.MAX_VALUE);
        }
        times.put(start, 0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            final Node current = pq.poll();
            // 현재 역 정보 가져오기
            final SubwayStation currentStation = current.station;

            if (!isContainsStation(currentStation)) {
                continue;
            }
            if (!visited.add(currentStation)) {
                continue;
            }
            if (end.equals(currentStation)) {
                break;
            }

            Set<Edge> currentEdges = getEdges(currentStation);
            
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
                            if (currentEdge.isTowards(currentStation) && currentEdge.isSameLine(currentLine)) {
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

    private Set<Edge> getEdges(final SubwayStation currentStation) {
        return subwayEdges.stream()
                .filter(edgeSet -> edgeSet.isSameStation(currentStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("현재 역에 해당하는 SubwayEdge가 존재하지 않습니다. 역 이름: " + currentStation.getName()))
                .getEdges();
    }

    private StationSequence reconstructPaths(
            final Map<SubwayStation, List<PreviousInfo>> prev,
            final SubwayStation start,
            final SubwayStation end
    ) {
        final List<StationSegment> fullSegments = new ArrayList<>();
        SubwayStation current = end;

        fullSegments.addFirst(new StationSegment(current, null));

        List<PreviousInfo> previousInfos = prev.get(current);
        while (previousInfos != null) {
            if (!isContainsStation(current)) {
                log.error("노선도에 존재하지 않는 지하철 역: {}", current.getName());
                throw new IllegalStateException("찾으려는 이름과 일치하는 역이 노선도에 존재하지 않습니다.");
            }

            SubwayStation next = current;
            // 현재 역에서 이전 역으로 이동할 호선 선택하기
            SubwayLine targetLine = null;

            if (previousInfos.size() == 1) {
                current = previousInfos.getFirst().station;
                targetLine = previousInfos.getFirst().line;
            } else {
                for (PreviousInfo previousInfo : previousInfos) {
                    current = previousInfo.station;

                    if (!isContainsStation(current)) {
                        log.error("노선도에 존재하지 않는 지하철 역: {}", current.getName());
                        throw new IllegalStateException("찾으려는 이름과 일치하는 역이 노선도에 존재하지 않습니다.");
                    }

                    final SubwayLine nextLine = previousInfo.line;

                    if (!end.equals(next)) {
                        if (nextLine.equals(fullSegments.getFirst().getLine())) {
                            targetLine = nextLine;
                            break;
                        }

                    } else if (!start.equals(current)) {
                        final List<PreviousInfo> beforeCurrentStations = prev.get(current);
                        if (beforeCurrentStations == null) {
                            throw new IllegalStateException("현재 역이 출발역이 아니면 이전 역과 호선은 꼭 존재해야 합니다.");
                        }

                        final List<SubwayLine> currentLines = getPreviousLines(beforeCurrentStations);
                        if (currentLines.contains(nextLine)) {
                            targetLine = nextLine;
                            break;
                        }
                    } else {
                        targetLine = nextLine;
                    }
                }
            }

            // 현재 역에서 다음 역으로 가는 Edge 중에서 호선명이 일치하는 Edge 찾기
            Edge movementEdge = null;
            for (Edge edge : getEdges(current)) {
                if (edge.getDestination().equals(next) && edge.getSubwayLine().equals(targetLine)) {
                    movementEdge = edge;
                    break;
                }
            }
            if (movementEdge == null) {
                log.error(
                        "현재역: {}, 다음역: {}, 노선: {}",
                        current.getName(),
                        next.getName(),
                        targetLine == null ? "null" : targetLine.getTitle()
                );
                throw new IllegalStateException("다음 역으로 가는 Edge가 존재하지 않습니다.");
            }

            // StationSegment 생성하여 경로에 추가
            fullSegments.addFirst(new StationSegment(current, movementEdge));

            if (start.equals(current)) {
                break;
            }

            final List<PreviousInfo> beforeCurrentInfos = prev.get(current);
            if (beforeCurrentInfos == null) {
                throw new IllegalStateException("현재 역이 출발역이 아니면 이전 역과 호선은 꼭 존재해야 합니다.");
            }

            final List<SubwayLine> currentLines = getPreviousLines(beforeCurrentInfos);

            if (currentLines != null && !currentLines.contains(targetLine)) {
                Edge transferEdge = null;
                for (Edge edge : getEdges(current)) {
                    if (edge.getDestination().equals(current) && edge.getSubwayLine().equals(targetLine)) {
                        transferEdge = edge;
                        break;
                    }
                }
                if (transferEdge == null) {
                    log.error(
                            "현재역: {}, 다음역: {}, 환승호선: {} -> {}",
                            current.getName(), next.getName(),
                            currentLines.getFirst().getTitle(),
                            targetLine.getTitle()
                    );
                    throw new IllegalStateException("환승역이지만 환승 Edge가 존재하지 않습니다.");
                }
                fullSegments.addFirst(new StationSegment(current, transferEdge));
            }

            previousInfos = beforeCurrentInfos;
        }
        if (!start.equals(current)) {
            throw new IllegalStateException("경로가 출발역까지 이어지지 않습니다.");
        }

        return new StationSequence(fullSegments);
    }

    private boolean isContainsStation(final SubwayStation station) {
        return subwayEdges.stream()
                .anyMatch(edgeSet -> edgeSet.isSameStation(station));
    }

    private Set<SubwayStation> getAllStations() {
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
    }

}
