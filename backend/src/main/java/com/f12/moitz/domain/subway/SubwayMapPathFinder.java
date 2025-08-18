package com.f12.moitz.domain.subway;

import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.TravelMethod;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class SubwayMapPathFinder {

    private final Map<String, SubwayStation> stationMap;

    public SubwayMapPathFinder(final Map<String, SubwayStation> stationMap) {
        this.stationMap = stationMap;
    }

    public Mono<List<Path>> findShortestTimePathMono(final String startName, final String endName) {
        return Mono.just(findShortestTimePath(startName, endName));
    }

    public List<Path> findShortestTimePath(final String startName, final String endName) {
        if (!stationMap.containsKey(startName) || !stationMap.containsKey(endName)) {
            log.error("노선도에 존재하지 않는 역입니다. 출발역: {}, 도착역: {}", startName, endName);
            throw new IllegalStateException("출발역 또는 도착역이 노선도에 존재하지 않아 경로를 찾을 수 없습니다.");
        }
        if (startName.equals(endName)) {
            throw new IllegalStateException("출발역과 도착역은 동일할 수 없습니다.");
        }

        Map<String, Integer> times = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Map<String, String> edgeLines = new HashMap<>(); // 각 역에 도달할 때 사용한 호선 저장
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.time));
        Set<String> visited = new HashSet<>();

        // 초기화
        for (String stationName : stationMap.keySet()) {
            times.put(stationName, Integer.MAX_VALUE);
        }
        times.put(startName, 0);
        pq.add(new Node(startName, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (!visited.add(current.name)) {
                continue;
            }

            if (current.name.equals(endName)) {
                break;
            }

            SubwayStation currentStation = stationMap.get(current.name);
            if (currentStation == null) {
                continue;
            }

            for (Edge edge : currentStation.getPossibleEdges()) {
                String neighborName = edge.getDestination();

                if (visited.contains(neighborName)) {
                    continue;
                }

                if (times.get(current.name) == null || times.get(neighborName) == null) {
                    throw new IllegalStateException("출발역 혹은 도착역에 도달하는 시간이 초기화되지 않았습니다.");
                }

                int newTime = times.get(current.name) + edge.getTimeInSeconds();

                // 환승 시간 추가
                if (!startName.equals(current.name) && !edgeLines.get(current.name).equals(edge.getLineName())) {
                    Edge transferEdge = null;
                    for (Edge currentEdge : currentStation.getPossibleEdges()) {
                        if (currentEdge.isTowards(current.name) && currentEdge.isSameLine(edge.getLineName())) {
                            transferEdge = currentEdge;
                            break;
                        }
                    }
                    if (transferEdge == null) {
                        log.error("현재역: {}, 다음역: {}, 환승호선: {}", current.name, neighborName, edge.getLineName());
                        throw new IllegalStateException("환승 시간을 계산할 환승 Edge가 존재하지 않습니다.");
                    }

                    newTime += transferEdge.getTimeInSeconds();
                }

                if (newTime < times.get(neighborName)) {
                    times.put(neighborName, newTime);
                    prev.put(neighborName, current.name);
                    edgeLines.put(neighborName, edge.getLineName()); // 이 역에 도달한 호선 저장
                    pq.add(new Node(neighborName, newTime));
                }
            }
        }

        List<StationWithEdge> fullPath = reconstructPaths(prev, edgeLines, startName, endName);
        return groupByLine(fullPath);
    }

    private List<StationWithEdge> reconstructPaths(final Map<String, String> prev, final Map<String, String> edgeLines,
                                                   final String startName, final String endName) {
        List<StationWithEdge> fullPath = new ArrayList<>();
        String currentName = endName;

        // 먼저 도착역을 추가 (마지막 역이므로 Edge는 null)
        fullPath.addFirst(new StationWithEdge(currentName, null));

        // 역방향으로 경로 추적
        String previousName = prev.get(currentName);
        while (previousName != null) {
            String nextName = currentName; // 현재 추적 중인 역이 다음 역이 됨
            currentName = previousName;

            SubwayStation currentStation = stationMap.get(currentName);
            if (currentStation == null) {
                throw new IllegalStateException("찾으려는 이름과 일치하는 역이 노선도에 존재하지 않습니다.");
            }

            // edgeLines에서 다음 역(nextName)에 도달할 때 사용한 호선 정보를 가져옴
            String targetLineName = edgeLines.get(nextName);

            // 현재 역에서 다음 역으로 가는 Edge 중에서 호선명이 일치하는 Edge 찾기
            Edge movementEdge = null;
            for (Edge edge : currentStation.getPossibleEdges()) {
                if (edge.getDestination().equals(nextName) && edge.getLineName().equals(targetLineName)) {
                    movementEdge = edge;
                    break;
                }
            }
            if (movementEdge == null) {
                log.error("현재역: {}, 다음역: {}, 환승호선: {}", currentName, nextName, targetLineName);
                throw new IllegalStateException("다음 역으로 가는 Edge가 존재하지 않습니다.");
            }

            // StationWithEdge 생성하여 경로에 추가
            fullPath.addFirst(new StationWithEdge(currentName, movementEdge));

            if (startName.equals(currentName)) {
                break;
            }

            String currentLineName = edgeLines.get(currentName);
            if (!currentLineName.equals(targetLineName)) {
                Edge transferEdge = null;
                for (Edge edge : currentStation.getPossibleEdges()) {
                    if (edge.getDestination().equals(currentName) && edge.getLineName().equals(targetLineName)) {
                        transferEdge = edge;
                        break;
                    }
                }
                if (transferEdge == null) {
                    log.error("현재역: {}, 다음역: {}, 환승호선: {}", currentName, nextName, targetLineName);
                    throw new IllegalStateException("환승역이지만 환승 Edge가 존재하지 않습니다.");
                }
                fullPath.addFirst(new StationWithEdge(currentName, transferEdge));
            }

            previousName = prev.get(currentName);
        }

        if (!startName.equals(currentName)) {
            throw new IllegalStateException("경로가 출발역까지 이어지지 않습니다.");
        }

        return fullPath;
    }

    private List<Path> groupByLine(List<StationWithEdge> fullPath) {
        List<Path> paths = new ArrayList<>();

        if (fullPath.isEmpty()) {
            throw new IllegalStateException("경로가 존재하지 않습니다.");
        }

        String currentLine = fullPath.getFirst().getLineName();
        String startStation = fullPath.getFirst().stationName;
        int totalTime = 0;

        for (int i = 1; i < fullPath.size(); i++) {
            StationWithEdge current = fullPath.get(i);

            // 이전 구간의 실제 이동 시간 계산 (Edge에서 직접 가져오기)
            StationWithEdge previous = fullPath.get(i - 1);
            int segmentTime = previous.edge != null ? previous.getTimeInSeconds() : 120;
            totalTime += segmentTime;

            // 환승 경로이거나 마지막 역인 경우
            if (current.isTransfer() || fullPath.getLast().equals(current)) {
                String endStation = current.stationName;

                // TODO: 정확한 좌표 조회해서 Point 생성하기
                Place fromPlace = new Place(startStation, new Point(125.0, 37.0));
                Place toPlace = new Place(endStation, new Point(125.0, 37.0));

                Path path = new Path(
                        fromPlace,
                        toPlace,
                        TravelMethod.SUBWAY,
                        totalTime,
                        currentLine
                );
                paths.add(path);

                // 환승 처리: 같은 역에서 다른 호선으로 환승 (마지막 역이 아닌 경우에만)
                if (current.isTransfer() && i < fullPath.size() - 1) {
                    // TODO: 정확한 좌표 조회해서 Point 생성하기
                    Place transferFrom = new Place(endStation, new Point(125.0, 37.0));

                    // 환승 시간 계산: 환승 Edge의 시간 직접 활용
                    int transferTime = current.getTimeInSeconds();

                    Path transferPath = new Path(
                            transferFrom,
                            transferFrom,
                            TravelMethod.TRANSFER,
                            transferTime,
                            null
                    );
                    paths.add(transferPath);

                    // 다음 구간 초기화
                    i++;
                    StationWithEdge next = fullPath.get(i);
                    currentLine = next.getLineName();
                    startStation = current.stationName;
                    totalTime = 0;
                }
            }
        }

        return paths;
    }

    private static class StationWithEdge {
        final String stationName;
        final Edge edge;

        StationWithEdge(String stationName, Edge edge) {
            this.stationName = stationName;
            this.edge = edge;
        }

        public boolean isTransfer() {
            if (edge == null) {
                return false;
            }
            return stationName.equals(edge.getDestination());
        }

        int getTimeInSeconds() {
            return edge.getTimeInSeconds();
        }

        String getLineName() {
            return edge != null ? edge.getLineName() : "도착역입니다.";
        }
    }

    private static class Node {
        String name;
        int time;

        Node(String name, int time) {
            this.name = name;
            this.time = time;
        }
    }

}
