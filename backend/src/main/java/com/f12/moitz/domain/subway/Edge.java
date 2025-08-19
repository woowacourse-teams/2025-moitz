package com.f12.moitz.domain.subway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Duration;
import lombok.Getter;

@Getter
public class Edge {

    @Field("destination")
    private final String destination;

    @Field("travelTime")
    private final Duration travelTime;

    @Field("distance")
    private final int distance;

    @Field("lineName")
    private final String lineName;

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
