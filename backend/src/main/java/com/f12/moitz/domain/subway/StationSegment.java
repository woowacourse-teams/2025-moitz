package com.f12.moitz.domain.subway;

import lombok.Getter;
import org.springframework.lang.Nullable;

public class StationSegment {

    @Getter
    private final SubwayStation station;
    @Nullable
    private final Edge edge;

    public StationSegment(final SubwayStation station, @Nullable final Edge edge) {
        validateStation(station);
        this.station = station;
        this.edge = edge;
    }

    private void validateStation(final SubwayStation station) {
        if (station == null) {
            throw new IllegalArgumentException("지하철역은 꼭 존재해야 합니다.");
        }
    }

    public boolean isTransfer() {
        if (edge == null) {
            return false;
        }
        return station.equals(edge.getDestination());
    }

    public int getTimeInSeconds() {
        if (edge == null) {
            return 0;
        }
        return edge.getTimeInSeconds();
    }

    public SubwayLine getLine() {
        if (edge == null) {
            return null;
        }
        return edge.getSubwayLine();
    }

}
