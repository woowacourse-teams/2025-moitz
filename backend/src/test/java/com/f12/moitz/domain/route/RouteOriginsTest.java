package com.f12.moitz.domain.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteOriginsTest {

    @Test
    @DisplayName("출발지와 도착지 목록으로 모든 출발도착 조합을 생성한다")
    void createOriginDestinationsTo() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));

        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(List.of(seolleung));

        assertThat(originDestinations.size()).isEqualTo(2);
        assertThat(originDestinations.getValues())
                .extracting(OriginDestination::getOrigin)
                .containsExactly(gangnam, yeoksam);
        assertThat(originDestinations.getValues())
                .extracting(OriginDestination::getDestination)
                .containsExactly(seolleung, seolleung);
    }

    @Test
    @DisplayName("출발지 사이의 출발도착 조합을 중복 없이 생성한다")
    void createOriginDestinationsBetweenOrigins() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam, seolleung));

        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();

        assertThat(originDestinations.size()).isEqualTo(3);
        assertThat(originDestinations.getValues())
                .extracting(OriginDestination::getOrigin)
                .containsExactly(gangnam, gangnam, yeoksam);
        assertThat(originDestinations.getValues())
                .extracting(OriginDestination::getDestination)
                .containsExactly(yeoksam, seolleung, seolleung);
    }

    @Test
    @DisplayName("목적지 후보 목록에서 출발지를 제외한다")
    void excludeOriginsFrom() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));

        final List<Place> destinations = routeOrigins.excludeOriginsFrom(List.of(gangnam, yeoksam, seolleung));

        assertThat(destinations).containsExactly(seolleung);
    }

    @Test
    @DisplayName("목적지 후보 목록은 null이거나 null을 포함할 수 없다")
    void excludeOriginsFrom_ThrowsExceptionWhenDestinationsAreInvalid() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam));

        assertThatThrownBy(() -> routeOrigins.excludeOriginsFrom(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로 도착지는 null일 수 없습니다.");
        assertThatThrownBy(() -> routeOrigins.excludeOriginsFrom(Arrays.asList(gangnam, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로 도착지는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("출발지는 중복될 수 없다")
    void throwsExceptionWhenOriginsAreDuplicated() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));

        assertThatThrownBy(() -> new RouteOrigins(List.of(gangnam, gangnam)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로 출발지는 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("출발지 간 경로의 이동시간으로 분산도 정책을 판정한다")
    void resolveDispersionPolicy() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam, seolleung));
        final List<Route> pairRoutes = List.of(
                route(gangnam, yeoksam, 30),
                route(gangnam, seolleung, 50),
                route(yeoksam, seolleung, 40)
        );

        final DispersionPolicy dispersionPolicy = routeOrigins.resolveDispersionPolicy(pairRoutes);

        assertThat(dispersionPolicy).isEqualTo(DispersionPolicy.TIER_2);
    }

    @Test
    @DisplayName("출발지 간 경로 목록은 null일 수 없다")
    void resolveDispersionPolicy_ThrowsExceptionWhenPairRoutesAreInvalid() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final RouteOrigins routeOrigins = new RouteOrigins(List.of(gangnam, yeoksam));

        assertThatThrownBy(() -> routeOrigins.resolveDispersionPolicy(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발지 간 경로는 null일 수 없습니다.");
        assertThatThrownBy(() -> routeOrigins.resolveDispersionPolicy(Arrays.asList(route(gangnam, yeoksam, 10), null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발지 간 경로는 null일 수 없습니다.");
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
