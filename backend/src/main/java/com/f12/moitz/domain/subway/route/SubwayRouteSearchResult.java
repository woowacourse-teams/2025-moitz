package com.f12.moitz.domain.subway.route;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.SubwayRouteException;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubwayRouteSearchResult {

    private final Map<SubwayStation, List<PreviousStation>> previousStations;

    public SubwayRouteSearchResult(final Map<SubwayStation, List<PreviousStation>> previousStations) {
        this.previousStations = Map.copyOf(previousStations);
    }

    public List<PreviousStation> getPreviousStations(final SubwayStation station) {
        final List<PreviousStation> previous = previousStations.get(station);
        if (previous == null || previous.isEmpty()) {
            throw new SubwayRouteException(
                    ExternalApiErrorCode.SUBWAY_ROUTE_CALCULATION_FAILED,
                    "경로가 출발역까지 이어지지 않습니다."
            );
        }
        return new ArrayList<>(previous);
    }

    public record PreviousStation(SubwayStation station, SubwayLine line) {

        public boolean isSameLine(final SubwayLine line) {
            return this.line == line;
        }

        public boolean canContinueFromPrevious(final SubwayRouteSearchResult searchResult) {
            return searchResult.getPreviousStations(station).stream()
                    .anyMatch(info -> info.isSameLine(line));
        }

    }

}
