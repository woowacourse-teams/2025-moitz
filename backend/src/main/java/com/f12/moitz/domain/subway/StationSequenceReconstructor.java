package com.f12.moitz.domain.subway;

import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.SubwayRouteException;
import com.f12.moitz.domain.subway.SubwayRouteSearchResult.PreviousStation;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StationSequenceReconstructor {

    private final SubwayEdges edges;

    public StationSequenceReconstructor(final SubwayEdges edges) {
        this.edges = edges;
    }

    public StationSequence reconstruct(
            final SubwayRouteSearchResult searchResult,
            final SubwayStation start,
            final SubwayStation end
    ) {
        final List<StationSegment> fullSegments = new ArrayList<>();
        SubwayStation current = end;
        SubwayLine preferredLine = null;

        fullSegments.addFirst(new StationSegment(current, null));

        while (!start.equals(current)) {
            final List<PreviousStation> previousStations = searchResult.getPreviousStations(current);
            final PreviousStation selected = selectPreviousStation(
                    previousStations,
                    searchResult,
                    start,
                    preferredLine
            );
            final boolean needsTransfer = preferredLine != null && !selected.isSameLine(preferredLine);

            final SubwayStation currentStation = current;
            final SubwayStation previousStation = selected.station();
            final SubwayLine selectedLine = selected.line();

            if (needsTransfer) {
                final Edge transferEdge = getEdgeBy(currentStation, currentStation, preferredLine);
                fullSegments.addFirst(new StationSegment(currentStation, transferEdge));
            }

            final Edge movementEdge = getEdgeBy(previousStation, currentStation, selectedLine);
            fullSegments.addFirst(new StationSegment(previousStation, movementEdge));

            current = previousStation;
            preferredLine = selectedLine;
        }

        return new StationSequence(fullSegments);
    }

    private PreviousStation selectPreviousStation(
            final List<PreviousStation> previousStations,
            final SubwayRouteSearchResult searchResult,
            final SubwayStation start,
            final SubwayLine preferredLine
    ) {
        if (preferredLine != null) {
            for (PreviousStation candidate : previousStations) {
                if (candidate.isSameLine(preferredLine)) {
                    return candidate;
                }
            }
        }

        for (PreviousStation candidate : previousStations) {
            if (start.equals(candidate.station())) {
                return candidate;
            }
            if (candidate.canContinueFromPrevious(searchResult)) {
                return candidate;
            }
        }

        return previousStations.getFirst();
    }

    private Edge getEdgeBy(final SubwayStation from, final SubwayStation to, final SubwayLine line) {
        return edges.findEdgeBy(from, to, line)
                .orElseThrow(() -> {
                    log.error("현재역: {}, 다음역: {}, 노선: {}", from.getName(), to.getName(), line.getTitle());
                    return new SubwayRouteException(
                            ExternalApiErrorCode.SUBWAY_ROUTE_CALCULATION_FAILED,
                            "다음 역으로 가는 Edge가 존재하지 않습니다."
                    );
                });
    }

}
