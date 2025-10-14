package com.f12.moitz.domain.subway;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Edge {

    private SubwayStation destination;
    private Duration travelTime;
    private int distance;
    private SubwayLine subwayLine;

    public Edge(
            final SubwayStation destination,
            final int timeInSeconds,
            final int distance,
            final String subwayLine
    ) {
        validate(destination, timeInSeconds, distance);
        this.destination = destination;
        this.travelTime = Duration.ofSeconds(timeInSeconds);
        this.distance = distance;
        this.subwayLine = subwayLine != null ? SubwayLine.fromTitle(subwayLine) : null;
    }

    private void validate(
            final SubwayStation destination,
            final int timeInSeconds,
            final int distance
    ) {
        if (destination == null) {
            throw new IllegalArgumentException("목적지는 비어있을 수 없습니다.");
        }
        if (timeInSeconds < 0) {
            throw new IllegalStateException("이동 시간은 음수일 수 없습니다.");
        }
        if (distance < 0) {
            throw new IllegalStateException("이동 거리는 음수일 수 없습니다.");
        }

    }

    public boolean isEqualTo(final Edge edge) {
        return this.destination.getName().equals(edge.getDestination().getName())
                && this.subwayLine.equals(edge.getSubwayLine());
    }

    public int getTimeInSeconds() {
        return (int) travelTime.getSeconds();
    }

    public boolean isSameLine(final SubwayLine subwayLine) {
        return this.subwayLine == subwayLine;
    }

    public boolean isTowards(final SubwayStation destination) {
        return this.destination.equals(destination);
    }

}
