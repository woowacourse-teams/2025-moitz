package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidateTest {

    @Test
    @DisplayName("예외가 발생하지 않고 후보가 생성된다")
    void doesNotThrow() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", "카페", 5, "url");
        final List<RecommendedPlace> recommendedPlaces = List.of(recommendedPlace);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Candidate(endPlace, routes, recommendedPlaces, "123", "123"));
    }

    @Test
    @DisplayName("필수 인자가 null이거나 비어있다면 후보를 생성할 수 없다")
    void isThrownByInvalidArguments() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", "카페", 5, "url");
        final List<RecommendedPlace> recommendedPlaces = List.of(recommendedPlace);

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Candidate(null, routes, recommendedPlaces, "123", "123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 지역은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, null, recommendedPlaces, "123", "123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("경로 목록은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, routes, null, "123", "123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 비어 있을 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, routes, Collections.emptyList(), "123", "123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 비어 있을 수 없습니다.");
        });
    }

    @Test
    @DisplayName("평균 소요 시간을 올바르게 계산한다")
    void calculateAverageTravelTime() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 600, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 1200, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", "카페", 5, "url");
        final List<RecommendedPlace> recommendedPlaces = List.of(recommendedPlace);
        final Candidate candidate = new Candidate(endPlace, routes, recommendedPlaces, "123", "123");

        // When
        final int averageTravelTime = candidate.calculateAverageTravelTime();

        // Then
        assertThat(averageTravelTime).isEqualTo(30);
    }
}
