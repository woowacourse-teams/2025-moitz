package com.f12.moitz.domain.subway;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

@Getter
public class SubwayEdge {

    private final SubwayStation subwayStation;
    private final Set<Edge> edges;

    public SubwayEdge(final SubwayStation subwayStation) {
        this.subwayStation = subwayStation;
        this.edges = new HashSet<>();
    }

    public void addEdge(final Edge newEdge) {
        final Optional<Edge> existingEdge = edges.stream()
                .filter(edge -> edge.isEqualTo(newEdge))
                .findFirst();

        if (existingEdge.isEmpty()) {
            edges.add(newEdge);
        }
    }

    public boolean isSameStation(final SubwayStation otherStation) {
        return this.subwayStation.equals(otherStation);
    }

    public Optional<Edge> findEdgeBy(final SubwayStation from, final SubwayStation to, final SubwayLine line) {
        if (!subwayStation.equals(from)) {
            return Optional.empty();
        }
        return edges.stream()
                .filter(edge -> edge.hasSameValue(to, line))
                .findFirst();
    }

}
