package com.f12.moitz.domain.subway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Document(collection = "subway-stations")
public class SubwayStation {

    @Id
    private String name;

    private List<Edge> possibleEdges;

    protected SubwayStation() {}

    public SubwayStation(final String name) {
        this.name = name;
        this.possibleEdges = new ArrayList<>();
    }

    public void addEdge(final Edge newEdge) {
        final Optional<Edge> existingEdge = possibleEdges.stream()
                .filter(edge -> edge.isEqualTo(newEdge))
                .findFirst();

        if (existingEdge.isEmpty()) {
            possibleEdges.add(newEdge);
        }
    }
}