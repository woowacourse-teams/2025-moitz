package com.f12.moitz.domain.subway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class SubwayStation {

    private final String name;
    private final List<Edge> possibleEdges;

    public SubwayStation(String name) {
        this.name = name;
        this.possibleEdges = new ArrayList<>();
    }

    @JsonCreator
    public SubwayStation(
            @JsonProperty("name") String name,
            @JsonProperty("possibleEdges") List<Edge> possibleEdges
    ) {
        this.name = name;
        this.possibleEdges = possibleEdges != null ? possibleEdges : new ArrayList<>();
    }

    public void addEdge(Edge newEdge) {
        Optional<Edge> existingEdge = possibleEdges.stream()
                .filter(edge -> edge.isEqualTo(newEdge))
                .findFirst();

        if (existingEdge.isEmpty()) {
            possibleEdges.add(newEdge);
        }
    }

    public Optional<Edge> findEdgeTo(final String destination) {
        return possibleEdges.stream()
                .filter(edge -> edge.isTowards(destination))
                .findFirst();
    }

}
