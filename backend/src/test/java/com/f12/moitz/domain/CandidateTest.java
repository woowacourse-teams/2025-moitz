package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CandidateTest {

    private static final Point DEFAULT_POINT = new Point(127.2, 37.21);

    @Test
    @DisplayName("예외가 발생하지 않고 후보가 생성된다")
    void doesNotThrow() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final List<Point> points = List.of(startPlace.getPoint(), intermediatePlace.getPoint(), endPlace.getPoint());
        final Course course = new Course(points);
        final Courses courses = new Courses(List.of(course));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url","imageUrl");
        Map<RecommendCondition, List<RecommendedPlace>> recommendedPlacesByCondition = Map.of(RecommendCondition.CAFE, List.of(recommendedPlace));
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(recommendedPlacesByCondition);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Candidate(endPlace, routes, courses, recommendedPlaces, CandidateSelectionTag.GENERAL, "123", "123", 0));
    }

    @Test
    @DisplayName("필수 인자가 null이거나 비어있다면 후보를 생성할 수 없다")
    void isThrownByInvalidArguments() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final List<Point> points = List.of(startPlace.getPoint(), intermediatePlace.getPoint(), endPlace.getPoint());
        final Course course = new Course(points);
        final Courses courses = new Courses(List.of(course));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url","imageUrl");
        Map<RecommendCondition, List<RecommendedPlace>> recommendedPlacesByCondition = Map.of(RecommendCondition.CAFE, List.of(recommendedPlace));
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(recommendedPlacesByCondition);


        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Candidate(null, routes, courses, recommendedPlaces, CandidateSelectionTag.GENERAL, "123", "123", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 지역은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, null, courses, recommendedPlaces, CandidateSelectionTag.GENERAL, "123", "123", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("경로 목록은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, routes, null, recommendedPlaces, CandidateSelectionTag.GENERAL, "123", "123", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 코스 목록은 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(
                            endPlace,
                            routes,
                            new Courses(List.of(course, course)),
                            recommendedPlaces,
                            CandidateSelectionTag.GENERAL,
                            "123",
                            "123",
                            0
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("경로 목록과 이동 코스 목록의 개수는 같아야 합니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, routes, courses, null, CandidateSelectionTag.GENERAL, "123", "123", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 비어 있을 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Candidate(endPlace, routes, courses, new RecommendedPlaces(Collections.emptyMap()), CandidateSelectionTag.GENERAL, "123", "123", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 비어 있을 수 없습니다.");

            softAssertions.assertThat(new Candidate(
                            endPlace,
                            routes,
                            courses,
                            recommendedPlaces,
                            (CandidateSelectionTag) null,
                            "123",
                            "123",
                            0
                    ).getTag())
                    .isEqualTo(CandidateSelectionTag.GENERAL);
            softAssertions.assertThat(new Candidate(
                            endPlace,
                            routes,
                            courses,
                            recommendedPlaces,
                            (CandidateSelectionTag) null,
                            "123",
                            "123",
                            0
                    ).getTags())
                    .containsExactly(CandidateSelectionTag.GENERAL);
        });
    }

    @Test
    @DisplayName("후보는 여러 추천 태그를 가질 수 있다")
    void createWithMultipleTags() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final Route route = new Route(List.of(new Path(
                startPlace,
                endPlace,
                TravelMethod.SUBWAY,
                600,
                SubwayLine.fromTitle("2호선")
        )));
        final Routes routes = new Routes(List.of(route));
        final Courses courses = new Courses(List.of(new Course(List.of(startPlace.getPoint(), endPlace.getPoint()))));
        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url", "imageUrl");
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(
                Map.of(RecommendCondition.CAFE, List.of(recommendedPlace))
        );

        final Candidate candidate = new Candidate(
                endPlace,
                routes,
                courses,
                recommendedPlaces,
                List.of(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.EFFICIENCY),
                "123",
                "123",
                0
        );

        assertThat(candidate.getTags())
                .containsExactly(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.EFFICIENCY);
        assertThat(candidate.getTag()).isEqualTo(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("추천 이유를 바탕으로 후보를 생성한다")
    void createWithRecommendationReason() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final Route route = new Route(List.of(Path.subway(
                startPlace,
                endPlace,
                600,
                SubwayLine.fromTitle("2호선")
        )));
        final Routes routes = new Routes(List.of(route));
        final Courses courses = new Courses(List.of(new Course(List.of(startPlace.getPoint(), endPlace.getPoint()))));
        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url", "imageUrl");
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(
                Map.of(RecommendCondition.CAFE, List.of(recommendedPlace))
        );
        final RecommendationReason recommendationReason = new RecommendationReason("#공평", "이동 시간이 고르게 분산됩니다.");

        final Candidate candidate = Candidate.create(
                endPlace,
                recommendationReason,
                recommendedPlaces,
                routes,
                courses,
                List.of(CandidateSelectionTag.FAIRNESS)
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(candidate.getDescription()).isEqualTo("#공평");
            softAssertions.assertThat(candidate.getReason()).isEqualTo("이동 시간이 고르게 분산됩니다.");
            softAssertions.assertThat(candidate.getTag()).isEqualTo(CandidateSelectionTag.FAIRNESS);
            softAssertions.assertThat(candidate.getVotes()).isZero();
        });
    }

    @Test
    @DisplayName("저장 문서에 태그 정보가 없거나 비어있으면 종합 추천 태그로 보정한다")
    void getTags_UsesGeneralWhenTagsAndLegacyTagAreMissing() throws Exception {
        final var constructor = Candidate.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Candidate candidate = constructor.newInstance();

        assertThat(candidate.getTags()).containsExactly(CandidateSelectionTag.GENERAL);
        assertThat(candidate.getTag()).isEqualTo(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("저장 문서의 태그 목록에 null이 포함되어도 유효한 태그만 조회한다")
    void getTags_FiltersNullTags() throws Exception {
        final var constructor = Candidate.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Candidate candidate = constructor.newInstance();
        ReflectionTestUtils.setField(
                candidate,
                "tags",
                Arrays.asList(CandidateSelectionTag.FAIRNESS, null, CandidateSelectionTag.FAIRNESS)
        );

        assertThat(candidate.getTags()).containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(candidate.getTag()).isEqualTo(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("종합 추천 태그는 다른 태그가 없는 경우에만 조회한다")
    void getTags_RemovesGeneralWhenOtherTagsExist() throws Exception {
        final var constructor = Candidate.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Candidate candidate = constructor.newInstance();
        ReflectionTestUtils.setField(
                candidate,
                "tags",
                List.of(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.GENERAL)
        );

        assertThat(candidate.getTags()).containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(candidate.getTag()).isEqualTo(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("평균 소요 시간을 올바르게 계산한다")
    void calculateAverageTravelTime() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 600, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 1200, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final List<Point> points = List.of(startPlace.getPoint(), intermediatePlace.getPoint(), endPlace.getPoint());
        final Course course = new Course(points);
        final Courses courses = new Courses(List.of(course));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url","imageUrl");
        Map<RecommendCondition, List<RecommendedPlace>> recommendedPlacesByCondition = Map.of(RecommendCondition.CAFE, List.of(recommendedPlace));
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(recommendedPlacesByCondition);
        final Candidate candidate = new Candidate(endPlace, routes, courses, recommendedPlaces, CandidateSelectionTag.GENERAL, "123", "123", 0);

        // When
        final int averageTravelTime = candidate.calculateAverageTravelTime();

        // Then
        assertThat(averageTravelTime).isEqualTo(30);
    }

    @Test
    @DisplayName("후보의 공평 점수를 계산한다")
    void calculateFairnessScore() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final Routes routes = new Routes(List.of(
                new Route(List.of(Path.subway(startA, endPlace, 10 * 60, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(Path.subway(startB, endPlace, 20 * 60, SubwayLine.fromTitle("2호선"))))
        ));
        final Courses courses = new Courses(List.of(
                new Course(List.of(startA.getPoint(), endPlace.getPoint())),
                new Course(List.of(startB.getPoint(), endPlace.getPoint()))
        ));
        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url", "imageUrl");
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(
                Map.of(RecommendCondition.CAFE, List.of(recommendedPlace))
        );
        final Candidate candidate = new Candidate(
                endPlace,
                routes,
                courses,
                recommendedPlaces,
                CandidateSelectionTag.GENERAL,
                "123",
                "123",
                0
        );

        assertThat(candidate.calculateFairnessScore().getAverageTravelTime()).isEqualTo(15);
    }

    @Test
    @DisplayName("후보는 경로와 이동 코스를 같은 순서로 제공한다")
    void getRouteAndCourseByIndex() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final Route routeA = new Route(List.of(Path.subway(startA, endPlace, 10 * 60, SubwayLine.fromTitle("2호선"))));
        final Route routeB = new Route(List.of(Path.subway(startB, endPlace, 20 * 60, SubwayLine.fromTitle("2호선"))));
        final Course courseA = new Course(List.of(startA.getPoint(), endPlace.getPoint()));
        final Course courseB = new Course(List.of(startB.getPoint(), endPlace.getPoint()));
        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", DEFAULT_POINT, "카페", 5, "url", "imageUrl");
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(
                Map.of(RecommendCondition.CAFE, List.of(recommendedPlace))
        );
        final Candidate candidate = new Candidate(
                endPlace,
                new Routes(List.of(routeA, routeB)),
                new Courses(List.of(courseA, courseB)),
                recommendedPlaces,
                CandidateSelectionTag.GENERAL,
                "123",
                "123",
                0
        );

        assertThat(candidate.getRouteCount()).isEqualTo(2);
        assertThat(candidate.getRoute(0)).isEqualTo(routeA);
        assertThat(candidate.getCourse(0)).isEqualTo(courseA);
        assertThat(candidate.getRoute(1)).isEqualTo(routeB);
        assertThat(candidate.getCourse(1)).isEqualTo(courseB);
    }

}
