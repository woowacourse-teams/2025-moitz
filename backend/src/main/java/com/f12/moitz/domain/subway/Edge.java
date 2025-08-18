package com.f12.moitz.domain.subway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.Getter;

@Getter
public class Edge {

    private final String destination;
    private final Duration travelTime; // seconds
    private final int distance; // m
    private final String lineName;

    public Edge(String destination, int timeInSeconds, int distance, String lineName) {
        this.destination = destination;
        this.travelTime = Duration.ofSeconds(timeInSeconds);
        this.distance = distance;
        this.lineName = lineName;
    }

    @JsonCreator
    public Edge(
            @JsonProperty("destination") String destination,
            @JsonProperty("travelTime") Duration duration,
            @JsonProperty("distance") int distance,
            @JsonProperty("lineName") String lineName
    ) {
        this.destination = destination;
        this.travelTime = duration;
        this.distance = distance;
        this.lineName = lineName;
    }

    public boolean isEqualTo(Edge edge) {
        return this.destination.equals(edge.destination) && this.lineName.equals(edge.lineName);
    }

    public boolean isSameLine(String lineName) {
        return this.lineName.equals(lineName);
    }

    public boolean isTowards(String destination) {
        return this.destination.equals(destination);
    }

    public int getTimeInSeconds() {
        return (int) travelTime.getSeconds();
    }

    public boolean hasSameDestination(Edge other) {
        return this.destination.equals(other.destination);
    }

}
