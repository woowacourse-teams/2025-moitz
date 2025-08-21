package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationTest {

    @Test
    @DisplayName("예외가 발생하지 않고 추천이 생성된다")
    void doesNotThrow() {
        // Given
        final Candidate candidate = createCandidate(20, 10);
        final List<Candidate> candidates = List.of(candidate);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Recommendation(candidates));
    }

    @Test
    @DisplayName("후보지 목록이 null이거나 비어있다면 추천을 생성할 수 없다")
    void isThrownByInvalidCandidates() {
        // Given
        final List<Candidate> candidatesNull = null;
        final List<Candidate> candidatesEmpty = Collections.emptyList();

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Recommendation(candidatesNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보지는 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Recommendation(candidatesEmpty))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보지는 비어있거나 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("후보지들을 평균 소요 시간 순으로 정렬한다")
    void sortCandidates() {
        // Given
        final Candidate candidate1 = createCandidate(1500, 600);
        final Candidate candidate2 = createCandidate(1200, 600);
        final List<Candidate> candidates = List.of(candidate1, candidate2);

        // When
        final Recommendation recommendation = new Recommendation(candidates);

        // Then
        assertThat(recommendation.get(0)).isEqualTo(candidate2);
        assertThat(recommendation.get(1)).isEqualTo(candidate1);
    }

    @Test
    @DisplayName("최적의 추천 시간을 올바르게 반환한다")
    void getBestRecommendationTime() {
        // Given
        final Candidate candidate1 = createCandidate(1200, 600);
        final Candidate candidate2 = createCandidate(1800, 600);
        final List<Candidate> candidates = List.of(candidate1, candidate2);
        final Recommendation recommendation = new Recommendation(candidates);

        // When
        final int bestRecommendationTime = recommendation.getBestRecommendationTime();

        // Then
        assertThat(bestRecommendationTime).isEqualTo(30);
    }

    private Candidate createCandidate(int path1TravelTime, int path2TravelTime) {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, path1TravelTime, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, path2TravelTime, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", "카페", 5, "url");
        final List<RecommendedPlace> recommendedPlaces = List.of(recommendedPlace);
        return new Candidate(endPlace, routes, recommendedPlaces, "123", "123");
    }
}
