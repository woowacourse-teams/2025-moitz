package com.f12.moitz.domain.subway;

import lombok.Getter;

import java.time.Duration;

@Getter
public class Edge {

    private String destination;

    private Duration travelTime;

    private int distance;

    private String lineName;

    protected Edge() {}

    public Edge(final String destination, final int timeInSeconds, final int distance, final String lineName) {
        this.destination = destination;
        this.travelTime = Duration.ofSeconds(timeInSeconds);
        this.distance = distance;
        this.lineName = lineName;
    }

    public Edge(
            String destination,
            Duration duration,
            int distance,
            String lineName
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
}
