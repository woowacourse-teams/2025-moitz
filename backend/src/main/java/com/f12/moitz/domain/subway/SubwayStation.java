package com.f12.moitz.domain.subway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
@Document(collection = "subway-stations")
public class SubwayStation {

    @Id
    private String name;

    @Field("possibleEdges")
    private final List<Edge> possibleEdges;

    protected SubwayStation() {
        this.possibleEdges = new ArrayList<>();
    }

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