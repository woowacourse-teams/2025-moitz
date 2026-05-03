package com.f12.moitz.domain.subway;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.CandidateRoute;
import com.f12.moitz.domain.Point;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationSequenceTest {

    @Test
    @DisplayName("역 순서로부터 후보 경로를 생성한다")
    void toCandidateRoute() {
        final SubwayStation start = new SubwayStation("강남역", new Point(127.027, 37.497));
        final SubwayStation end = new SubwayStation("역삼역", new Point(127.036, 37.501));
        final StationSequence stationSequence = new StationSequence(
                List.of(
                        new StationSegment(start, new Edge(end, 180, 0, "2호선")),
                        new StationSegment(end, null)
                )
        );

        final CandidateRoute candidateRoute = stationSequence.toCandidateRoute();

        assertThat(candidateRoute.getRoute().getPaths()).hasSize(1);
        assertThat(candidateRoute.getRoute().getStartPlace()).isEqualTo(start);
        assertThat(candidateRoute.getRoute().getEndPlace()).isEqualTo(end);
        assertThat(candidateRoute.getRoute().calculateTotalTravelTime()).isEqualTo(3);
        assertThat(candidateRoute.getCourse().getPoints()).containsExactly(start.getPoint(), end.getPoint());
    }

}
