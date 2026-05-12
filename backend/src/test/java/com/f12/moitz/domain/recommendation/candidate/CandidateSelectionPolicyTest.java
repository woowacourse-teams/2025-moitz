package com.f12.moitz.domain.recommendation.candidate;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidateSelectionPolicyTest {

    private final CandidateSelectionPolicy policy = new CandidateSelectionPolicy();

    @Test
    @DisplayName("분산도가 큰 요청에서는 공평성 후보를 장소 검색 대상에 포함한다")
    void select_IncludesFairnessTagCandidateForDispersedRequest() {
        final Place start1 = place("수원역");
        final Place start2 = place("강동역");
        final Place start3 = place("숭의역");
        final RouteCandidate candidate1 = routeCandidate("후보1역", start1, start2, start3, 35, 48, 60);
        final RouteCandidate candidate2 = routeCandidate("후보2역", start1, start2, start3, 36, 49, 61);
        final RouteCandidate candidate3 = routeCandidate("후보3역", start1, start2, start3, 37, 50, 62);
        final RouteCandidate candidate4 = routeCandidate("후보4역", start1, start2, start3, 38, 51, 63);
        final RouteCandidate candidate5 = routeCandidate("후보5역", start1, start2, start3, 39, 52, 64);
        final RouteCandidate fairnessCandidate = routeCandidate("공평후보역", start1, start2, start3, 70, 71, 72);
        final RouteCandidate candidate6 = routeCandidate("후보6역", start1, start2, start3, 40, 53, 65);

        final CandidateSelection result = policy.select(
                List.of(candidate1, candidate2, candidate3, candidate4, candidate5, fairnessCandidate, candidate6),
                DispersionPolicy.TIER_4,
                5
        );

        assertThat(CandidateSelectionTag.orderedValues())
                .allSatisfy(tag -> assertThat(result.getCandidatesByTag(tag)).isNotEmpty());
        assertThat(result.getSearchCandidatePlaces())
                .extracting(Place::getName)
                .contains("공평후보역");
    }

    private RouteCandidate routeCandidate(
            final String candidateName,
            final Place start1,
            final Place start2,
            final Place start3,
            final int firstMinutes,
            final int secondMinutes,
            final int thirdMinutes
    ) {
        final Place candidate = place(candidateName);
        return new RouteCandidate(
                candidate,
                new Routes(List.of(
                        route(start1, candidate, firstMinutes),
                        route(start2, candidate, secondMinutes),
                        route(start3, candidate, thirdMinutes)
                ))
        );
    }

    private Route route(final Place start, final Place end, final int minutes) {
        return new Route(List.of(new Path(
                start,
                end,
                TravelMethod.SUBWAY,
                minutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
