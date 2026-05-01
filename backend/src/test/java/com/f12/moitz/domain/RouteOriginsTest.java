package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsTo(List.of(seolleung));

        assertThat(originDestinations).hasSize(2);
        assertThat(originDestinations)
                .extracting(OriginDestination::getOrigin)
                .containsExactly(gangnam, yeoksam);
        assertThat(originDestinations)
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

        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();

        assertThat(originDestinations).hasSize(3);
        assertThat(originDestinations)
                .extracting(OriginDestination::getOrigin)
                .containsExactly(gangnam, gangnam, yeoksam);
        assertThat(originDestinations)
                .extracting(OriginDestination::getDestination)
                .containsExactly(yeoksam, seolleung, seolleung);
    }

    @Test
    @DisplayName("출발지는 중복될 수 없다")
    void throwsExceptionWhenOriginsAreDuplicated() {
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));

        assertThatThrownBy(() -> new RouteOrigins(List.of(gangnam, gangnam)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로 출발지는 중복될 수 없습니다.");
    }

}
