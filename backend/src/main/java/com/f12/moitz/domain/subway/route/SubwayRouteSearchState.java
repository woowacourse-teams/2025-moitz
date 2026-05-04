package com.f12.moitz.domain.subway.route;

import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.route.SubwayRouteSearchResult.PreviousStation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class SubwayRouteSearchState {

    private static final int UNREACHABLE_TIME = Integer.MAX_VALUE;

    private final Map<SubwayStation, Integer> times = new HashMap<>();
    private final Map<SubwayStation, List<PreviousStation>> previousStations = new HashMap<>();
    private final PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::time));
    private final Set<SubwayStation> visited = new HashSet<>();

    public SubwayRouteSearchState(final SubwayStation start) {
        times.put(start, 0);
        queue.add(new Node(start, 0));
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public SubwayStation pollStation() {
        return queue.poll().station();
    }

    public boolean visit(final SubwayStation station) {
        return visited.add(station);
    }

    public boolean isVisited(final SubwayStation station) {
        return visited.contains(station);
    }

    public int calculateTimeTo(final SubwayStation currentStation, final Edge edge) {
        return times.getOrDefault(currentStation, UNREACHABLE_TIME) + edge.getTimeInSeconds();
    }

    public boolean canContinueOn(final SubwayStation station, final SubwayLine line) {
        return previousStations.get(station).stream()
                .anyMatch(previousStation -> previousStation.isSameLine(line));
    }

    public SubwayLine getFirstPreviousLine(final SubwayStation station) {
        return previousStations.get(station).getFirst().line();
    }

    public void recordIfShorter(
            final SubwayStation previousStation,
            final SubwayStation nextStation,
            final SubwayLine line,
            final int time
    ) {
        final int currentTime = times.getOrDefault(nextStation, UNREACHABLE_TIME);

        if (time < currentTime) {
            times.put(nextStation, time);
            previousStations.put(nextStation, new ArrayList<>(List.of(new PreviousStation(previousStation, line))));
            queue.add(new Node(nextStation, time));
            return;
        }

        if (time == currentTime) {
            previousStations.get(nextStation)
                    .add(new PreviousStation(previousStation, line));
        }
    }

    public SubwayRouteSearchResult toResult() {
        return new SubwayRouteSearchResult(previousStations);
    }

    private record Node(SubwayStation station, int time) {

    }

}
