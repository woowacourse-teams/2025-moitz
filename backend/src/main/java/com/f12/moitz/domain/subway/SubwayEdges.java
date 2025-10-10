package com.f12.moitz.domain.subway;

import com.f12.moitz.domain.TravelMethod;
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

    public List<SubwayPath> findShortestTimePath(final SubwayStation start, final SubwayStation end) {
        if (!isContainsStation(start) || !isContainsStation(end)) {
            log.error("노선도에 존재하지 않는 역입니다. 출발역: {}, 도착역: {}", start.getName(), end.getName());
            throw new IllegalStateException("출발역 또는 도착역이 노선도에 존재하지 않아 경로를 찾을 수 없습니다.");
        }
        if (start.equals(end)) {
            log.error("동일한 출발역, 도착역: {}", start.getName());
            throw new IllegalStateException("출발역과 도착역은 동일할 수 없습니다.");
        }

        final Map<SubwayStation, Integer> times = new HashMap<>();
        final Map<SubwayStation, SubwayStation> prev = new HashMap<>();
        final Map<SubwayStation, List<SubwayLine>> edgeLines = new HashMap<>(); // 각 역에 도달할 때 사용 가능한 호선들 저장
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
            if (!visited.add(current.station)) {
                continue;
            }

            if (current.station.equals(end)) {
                break;
            }

            // 현재 역 정보 가져오기
            final SubwayStation currentStation = current.station;
            if (!isContainsStation(currentStation)) {
                continue;
            }

            for (Edge edge : getEdges(currentStation)) {
                final SubwayStation neighbor = edge.getDestination();
                if (visited.contains(neighbor)) {
                    continue;
                }

                if (times.get(currentStation) == null || times.get(neighbor) == null) {
                    log.warn("출발역({}) 혹은 도착역({})에 도달하는 시간이 초기화되지 않았습니다.", currentStation.getName(), neighbor.getName());
                    continue;
                }

                int newTime = times.get(currentStation) + edge.getTimeInSeconds();

                final List<SubwayLine> currentLines = edgeLines.get(currentStation);

                // 환승 시간 추가: 현재 역의 가능한 호선들 중 다음 간선의 호선이 포함되어 있지 않은 경우
                if (currentLines != null && !currentLines.contains(edge.getSubwayLine())) {
                    Edge transferEdge = null;
                    for (Edge currentEdge : getEdges(currentStation)) {
                        if (currentEdge.isTowards(currentStation) && currentEdge.isSameLine(edge.getSubwayLine())) {
                            transferEdge = currentEdge;
                            break;
                        }
                    }
                    if (transferEdge == null) {
                        log.warn(
                                "환승 Edge가 존재하지 않아 경로를 건너뜁니다. 현재역: {}, 다음역: {}, 환승호선: {} -> {}",
                                currentStation.getName(),
                                neighbor.getName(),
                                currentLines.getFirst().getTitle(),
                                edge.getSubwayLine().getTitle()
                        );
                        continue;
                    }

                    newTime += transferEdge.getTimeInSeconds();
                }

                if (newTime < times.get(neighbor)) {
                    times.put(neighbor, newTime);
                    prev.put(neighbor, currentStation);
                    pq.add(new Node(neighbor, newTime));

                    // 새로운 최단 시간이므로 호선 리스트를 새로 생성
                    final List<SubwayLine> lines = new ArrayList<>();
                    lines.add(edge.getSubwayLine());
                    edgeLines.put(neighbor, lines);
                } else if (newTime == times.get(neighbor)) {
                    // 같은 시간으로 도착 가능한 다른 호선 추가
                    final List<SubwayLine> lines = edgeLines.get(neighbor);
                    if (lines != null && !lines.contains(edge.getSubwayLine())) {
                        lines.add(edge.getSubwayLine());
                    }
                }
            }
        }

        final List<StationWithEdge> fullPath = reconstructPaths(prev, edgeLines, start, end);

        return groupByLine(fullPath);
    }

    private Set<Edge> getEdges(final SubwayStation currentStation) {
        return subwayEdges.stream()
                .filter(edgeSet -> edgeSet.isSameStation(currentStation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("현재 역에 해당하는 SubwayEdge가 존재하지 않습니다. 역 이름: " + currentStation.getName()))
                .getEdges();
    }

    private List<StationWithEdge> reconstructPaths(
            final Map<SubwayStation, SubwayStation> prev,
            final Map<SubwayStation, List<SubwayLine>> edgeLines,
            final SubwayStation start,
            final SubwayStation end
    ) {
        final List<StationWithEdge> fullPath = new ArrayList<>();
        SubwayStation current = end;

        fullPath.addFirst(new StationWithEdge(current, null));

        SubwayStation previous = prev.get(current);
        while (previous != null) {
            SubwayStation next = current;
            current = previous;

            if (!isContainsStation(current)) {
                log.error("노선도에 존재하지 않는 지하철 역: {}", current.getName());
                throw new IllegalStateException("찾으려는 이름과 일치하는 역이 노선도에 존재하지 않습니다.");
            }

            // edgeLines에서 현재 역(current)와 다음 역(next)에 도달할 때 사용 가능한 호선들을 가져옴
            final List<SubwayLine> currentLines = edgeLines.get(current);
            final List<SubwayLine> nextLines = edgeLines.get(next);
            if (nextLines == null) {
                throw new IllegalStateException("다음역으로 가는 호선은 꼭 존재해야 합니다.");
            }

            // 현재 역에서 다음 역으로 이동할 호선 선택하기
            SubwayLine targetLine = null;

            // 1순위: 환승 불필요 호선 선택 (다다음역의 호선과 같은 것 or 현재 역의 호선들 중 겹치는 것)
            if (!end.equals(next)) {
                for (SubwayLine nextLine : nextLines) {
                    if (nextLine.equals(fullPath.getFirst().edge.getSubwayLine())) {
                        targetLine = nextLine;
                        break;
                    }
                }
            } else if (!start.equals(current)) {
                for (SubwayLine nextLine : nextLines) {
                    if (currentLines.contains(nextLine)) {
                        targetLine = nextLine;
                        break;
                    }
                }
            }

            // 2순위: 겹치는 호선이 없으면 다음 역의 첫 번째 호선 선택 (환승 필요)
            if (targetLine == null) {
                targetLine = nextLines.getFirst();
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

            // StationWithEdge 생성하여 경로에 추가
            fullPath.addFirst(new StationWithEdge(current, movementEdge));

            if (start.equals(current)) {
                break;
            }

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
                fullPath.addFirst(new StationWithEdge(current, transferEdge));
            }

            previous = prev.get(current);
        }
        if (!start.equals(current)) {
            throw new IllegalStateException("경로가 출발역까지 이어지지 않습니다.");
        }

        return fullPath;
    }

    private List<SubwayPath> groupByLine(final List<StationWithEdge> fullPath) {
        final List<SubwayPath> paths = new ArrayList<>();

        if (fullPath.isEmpty()) {
            throw new IllegalStateException("경로가 존재하지 않습니다.");
        }

        SubwayLine currentLine = fullPath.getFirst().getLine();
        SubwayStation startStation = fullPath.getFirst().station;
        int totalTime = 0;

        for (int i = 1; i < fullPath.size(); i++) {
            final StationWithEdge current = fullPath.get(i);

            // 이전 구간의 실제 이동 시간 계산 (Edge에서 직접 가져오기)
            final StationWithEdge previous = fullPath.get(i - 1);
            final int segmentTime = previous.edge != null ? previous.getTimeInSeconds() : 120;
            totalTime += segmentTime;

            // 환승 경로이거나 마지막 역인 경우
            if (current.isTransfer() || fullPath.getLast().equals(current)) {
                final SubwayStation endStation = current.station;

                final SubwayPath path = new SubwayPath(
                        startStation,
                        endStation,
                        TravelMethod.SUBWAY,
                        totalTime,
                        currentLine
                );
                paths.add(path);

                // 환승 처리: 같은 역에서 다른 호선으로 환승 (마지막 역이 아닌 경우에만)
                if (current.isTransfer() && i < fullPath.size() - 1) {
                    // 환승 시간 계산: 환승 Edge의 시간 직접 활용
                    final int transferTime = current.getTimeInSeconds();

                    final SubwayPath transferPath = new SubwayPath(
                            endStation,
                            endStation,
                            TravelMethod.TRANSFER,
                            transferTime,
                            null
                    );
                    paths.add(transferPath);

                    // 다음 구간 초기화
                    i++;
                    final StationWithEdge next = fullPath.get(i);
                    currentLine = next.getLine();
                    startStation = current.station;
                    totalTime = 0;
                }
            }
        }

        return paths;
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

    private static class StationWithEdge {
        final SubwayStation station;
        final Edge edge;

        StationWithEdge(final SubwayStation station, final Edge edge) {
            this.station = station;
            this.edge = edge;
        }

        public boolean isTransfer() {
            if (edge == null) {
                return false;
            }
            return station.equals(edge.getDestination());
        }

        int getTimeInSeconds() {
            return edge.getTimeInSeconds();
        }

        SubwayLine getLine() {
            if (edge == null) {
                return null;
            }
            return edge.getSubwayLine();
        }
    }

    private static class Node {
        SubwayStation station;
        int time;

        Node(final SubwayStation station, final int time) {
            this.station = station;
            this.time = time;
        }
    }

}
