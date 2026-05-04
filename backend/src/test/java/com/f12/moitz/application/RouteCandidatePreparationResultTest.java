package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.recommendation.RouteCandidatePreparationResult;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.recommendation.candidate.RouteCandidate;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteCandidatePreparationResultTest {

    @Test
    @DisplayName("경로 후보 준비 결과를 보관한다")
    void getRouteCandidates() {
        final Place gangnam = place("강남역");
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final Routes seolleungRoutes = new Routes(List.of(route(gangnam, seolleung, 10)));
        final RouteCandidate routeCandidate = new RouteCandidate(seolleung, seolleungRoutes);

        final RouteCandidatePreparationResult result = new RouteCandidatePreparationResult(
                List.of(seolleung, samsung),
                Map.of(seolleung, seolleungRoutes),
                List.of(routeCandidate)
        );

        assertThat(result.getCandidatePlaces()).containsExactly(seolleung, samsung);
        assertThat(result.getCandidatePlaceCount()).isEqualTo(2);
        assertThat(result.getRoutedPlaceCount()).isEqualTo(1);
        assertThat(result.getRouteCandidates()).containsExactly(routeCandidate);
        assertThat(result.getRouteCandidates().getFirst().getRoutes()).isEqualTo(seolleungRoutes);
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

    private Route route(final Place origin, final Place destination, final int minutes) {
        return new Route(List.of(new Path(
                origin,
                destination,
                TravelMethod.SUBWAY,
                minutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

}
