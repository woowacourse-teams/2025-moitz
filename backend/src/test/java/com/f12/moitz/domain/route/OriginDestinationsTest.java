package com.f12.moitz.domain.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OriginDestinationsTest {

    @Test
    @DisplayName("경로 조회 결과를 도착지별 경로 목록으로 묶는다")
    void groupRoutesByDestination() {
        final Place gangnam = place("강남역");
        final Place yeoksam = place("역삼역");
        final Place seolleung = place("선릉역");
        final OriginDestinations originDestinations = new OriginDestinations(List.of(
                new OriginDestination(gangnam, seolleung),
                new OriginDestination(yeoksam, seolleung)
        ));
        final Route route1 = route(gangnam, seolleung, 10);
        final Route route2 = route(yeoksam, seolleung, 5);

        final Map<Place, Routes> routesByDestination = originDestinations.groupRoutesByDestination(List.of(
                route1,
                route2
        ));

        assertThat(routesByDestination).containsOnlyKeys(seolleung);
        assertThat(routesByDestination.get(seolleung).getRoutes()).containsExactly(route1, route2);
    }

    @Test
    @DisplayName("추천 후보 경로 조회 결과를 도착지별 후보 경로 목록으로 묶는다")
    void groupCandidateRoutesByDestination() {
        final Place gangnam = place("강남역");
        final Place yeoksam = place("역삼역");
        final Place seolleung = place("선릉역");
        final OriginDestinations originDestinations = new OriginDestinations(List.of(
                new OriginDestination(gangnam, seolleung),
                new OriginDestination(yeoksam, seolleung)
        ));
        final CandidateRoute candidateRoute1 = candidateRoute(gangnam, seolleung, 10);
        final CandidateRoute candidateRoute2 = candidateRoute(yeoksam, seolleung, 5);

        final Map<Place, List<CandidateRoute>> candidateRoutesByDestination = originDestinations
                .groupCandidateRoutesByDestination(List.of(candidateRoute1, candidateRoute2));

        assertThat(candidateRoutesByDestination).containsOnlyKeys(seolleung);
        assertThat(candidateRoutesByDestination.get(seolleung)).containsExactly(candidateRoute1, candidateRoute2);
    }

    @Test
    @DisplayName("경로 조회 결과 개수가 요청 개수와 다르면 도착지별 경로 목록으로 묶을 수 없다")
    void groupRoutesByDestination_ThrowsExceptionWhenRouteCountDoesNotMatch() {
        final Place gangnam = place("강남역");
        final Place seolleung = place("선릉역");
        final OriginDestinations originDestinations = new OriginDestinations(List.of(
                new OriginDestination(gangnam, seolleung)
        ));

        assertThatThrownBy(() -> originDestinations.groupRoutesByDestination(List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("후보 경로 조회 결과 개수가 일치하지 않습니다. 요청=1, 응답=0");
    }

    @Test
    @DisplayName("추천 후보 경로 조회 결과 개수가 요청 개수와 다르면 도착지별 후보 경로 목록으로 묶을 수 없다")
    void groupCandidateRoutesByDestination_ThrowsExceptionWhenCandidateRouteCountDoesNotMatch() {
        final Place gangnam = place("강남역");
        final Place seolleung = place("선릉역");
        final OriginDestinations originDestinations = new OriginDestinations(List.of(
                new OriginDestination(gangnam, seolleung)
        ));

        assertThatThrownBy(() -> originDestinations.groupCandidateRoutesByDestination(List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("추천 후보 경로 조회 결과 개수가 일치하지 않습니다. 요청=1, 응답=0");
    }

    @Test
    @DisplayName("출발도착 목록과 조회 결과 목록은 null을 포함할 수 없다")
    void validateNull() {
        final Place gangnam = place("강남역");
        final Place seolleung = place("선릉역");
        final OriginDestinations originDestinations = new OriginDestinations(List.of(
                new OriginDestination(gangnam, seolleung)
        ));

        assertThatThrownBy(() -> new OriginDestinations(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발도착 목록은 null일 수 없습니다.");
        assertThatThrownBy(() -> new OriginDestinations(Arrays.asList(
                new OriginDestination(gangnam, seolleung),
                null
        )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발도착 목록에 null이 포함될 수 없습니다.");
        assertThatThrownBy(() -> originDestinations.groupRoutesByDestination(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("후보 경로 조회 결과가 null입니다.");
        assertThatThrownBy(() -> originDestinations.groupRoutesByDestination(Collections.singletonList(null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("후보 경로 조회 결과에 null이 포함될 수 없습니다.");
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

    private CandidateRoute candidateRoute(final Place origin, final Place destination, final int minutes) {
        return new CandidateRoute(route(origin, destination, minutes), new Course(List.of(origin.getPoint(), destination.getPoint())));
    }

    private Route route(final Place origin, final Place destination, final int minutes) {
        return new Route(List.of(Path.subway(
                origin,
                destination,
                minutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

}
