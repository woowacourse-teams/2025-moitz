package com.f12.moitz.domain.recommendation.candidate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteCandidateScoresTest {

    @Test
    @DisplayName("후보별 공평 점수를 계산해 조회한다")
    void scoreOf() {
        final RouteCandidate candidate = routeCandidate("후보역", 10, 20);
        final RouteCandidateScores scores = new RouteCandidateScores(List.of(candidate));

        assertThat(scores.scoreOf(candidate).getAverageTravelTime()).isEqualTo(15);
    }

    @Test
    @DisplayName("계산된 공평 점수로 후보의 허용 여부를 판단한다")
    void isAcceptable() {
        final RouteCandidate candidate = routeCandidate("후보역", 10, 20);
        final RouteCandidateScores scores = new RouteCandidateScores(List.of(candidate));

        assertThat(scores.isAcceptable(candidate, DispersionPolicy.TIER_1)).isTrue();
    }

    @Test
    @DisplayName("점수를 계산하지 않은 후보는 조회할 수 없다")
    void scoreOf_ThrowsException_WhenCandidateIsMissing() {
        final RouteCandidate scoredCandidate = routeCandidate("점수후보역", 10, 20);
        final RouteCandidate missingCandidate = routeCandidate("누락후보역", 20, 30);
        final RouteCandidateScores scores = new RouteCandidateScores(List.of(scoredCandidate));

        assertThatThrownBy(() -> scores.scoreOf(missingCandidate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("후보 점수가 존재하지 않습니다.");
    }

    private RouteCandidate routeCandidate(
            final String candidateName,
            final int firstMinutes,
            final int secondMinutes
    ) {
        final Place candidate = place(candidateName);
        return new RouteCandidate(
                candidate,
                new Routes(List.of(
                        route(place("출발1역"), candidate, firstMinutes),
                        route(place("출발2역"), candidate, secondMinutes)
                ))
        );
    }

    private Route route(final Place start, final Place end, final int minutes) {
        return new Route(List.of(Path.subway(
                start,
                end,
                minutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
