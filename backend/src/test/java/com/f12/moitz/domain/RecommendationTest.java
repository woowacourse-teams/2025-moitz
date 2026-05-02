package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    @DisplayName("추천 생성 재료가 유효하지 않으면 추천을 생성할 수 없다")
    void create_IsThrownByInvalidCreationInputs() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final Map<Place, RecommendationReason> reasonsByPlace = Map.of(
                recommendedPlace,
                new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")
        );
        final Map<Place, CategorizedRecommendedPlaces> recommendedPlacesByPlace = Map.of(
                recommendedPlace,
                createRecommendedPlaces(RecommendCondition.CAFE)
        );
        final Map<Place, Routes> routesByPlace = Map.of(
                recommendedPlace,
                createRoutes(startPlace, recommendedPlace, 10 * 60)
        );
        final Map<Place, Courses> coursesByPlace = Map.of(
                recommendedPlace,
                createCourses(startPlace, recommendedPlace)
        );
        final Map<Place, List<CandidateSelectionTag>> tagsByPlace = Map.of(
                recommendedPlace,
                List.of(CandidateSelectionTag.FAIRNESS)
        );
        final List<RecommendCondition> recommendConditions = List.of(RecommendCondition.CAFE);

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            null,
                            recommendedPlacesByPlace,
                            routesByPlace,
                            coursesByPlace,
                            tagsByPlace,
                            0,
                            recommendConditions
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 이유는 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            reasonsByPlace,
                            null,
                            routesByPlace,
                            coursesByPlace,
                            tagsByPlace,
                            0,
                            recommendConditions
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 장소 목록은 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            reasonsByPlace,
                            recommendedPlacesByPlace,
                            null,
                            coursesByPlace,
                            tagsByPlace,
                            0,
                            recommendConditions
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 경로는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            reasonsByPlace,
                            recommendedPlacesByPlace,
                            routesByPlace,
                            null,
                            tagsByPlace,
                            0,
                            recommendConditions
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 이동 코스는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            reasonsByPlace,
                            recommendedPlacesByPlace,
                            routesByPlace,
                            coursesByPlace,
                            null,
                            0,
                            recommendConditions
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 태그는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> Recommendation.create(
                            reasonsByPlace,
                            recommendedPlacesByPlace,
                            routesByPlace,
                            coursesByPlace,
                            tagsByPlace,
                            0,
                            List.of()
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 조건은 비어있거나 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("후보지들을 태그 순서 우선으로 정렬한다")
    void sortCandidates() {
        // Given
        final Candidate generalCandidate = createCandidate(1200, 600, CandidateSelectionTag.GENERAL);
        final Candidate transferCandidate = createCandidate(1500, 600, CandidateSelectionTag.TRANSFER);
        final Candidate fairnessCandidate = createCandidate(1800, 600, CandidateSelectionTag.FAIRNESS);
        final List<Candidate> candidates = List.of(generalCandidate, transferCandidate, fairnessCandidate);

        // When
        final Recommendation recommendation = new Recommendation(candidates);

        // Then
        assertThat(recommendation.get(0)).isEqualTo(fairnessCandidate);
        assertThat(recommendation.get(1)).isEqualTo(transferCandidate);
        assertThat(recommendation.get(2)).isEqualTo(generalCandidate);
    }

    @Test
    @DisplayName("최적의 추천 시간을 올바르게 반환한다")
    void getBestRecommendationTime() {
        // Given
        final Candidate candidate1 = createCandidate(1200, 600, CandidateSelectionTag.GENERAL);
        final Candidate candidate2 = createCandidate(1800, 600, CandidateSelectionTag.FAIRNESS);
        final List<Candidate> candidates = List.of(candidate1, candidate2);
        final Recommendation recommendation = new Recommendation(candidates);

        // When
        final int bestRecommendationTime = recommendation.getBestRecommendationTime();

        // Then
        assertThat(bestRecommendationTime).isEqualTo(30);
    }

    @Test
    @DisplayName("추천 생성에 필요한 정보를 조합해 후보를 생성한다")
    void create() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final Routes routes = createRoutes(startPlace, recommendedPlace, 10 * 60);
        final Courses courses = createCourses(startPlace, recommendedPlace);
        final CategorizedRecommendedPlaces categorizedRecommendedPlaces = createRecommendedPlaces(RecommendCondition.CAFE);

        final Recommendation recommendation = Recommendation.create(
                Map.of(recommendedPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")),
                Map.of(recommendedPlace, categorizedRecommendedPlaces),
                Map.of(recommendedPlace, routes),
                Map.of(recommendedPlace, courses),
                Map.of(recommendedPlace, List.of(CandidateSelectionTag.FAIRNESS)),
                0,
                List.of(RecommendCondition.CAFE)
        );

        assertThat(recommendation.size()).isEqualTo(1);
        assertThat(recommendation.get(0).getDestination()).isEqualTo(recommendedPlace);
        assertThat(recommendation.get(0).getTags()).containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendation.get(0).getDescription()).isEqualTo("#공평");
        assertThat(recommendation.get(0).getReason()).isEqualTo("이동 시간이 고른 후보입니다.");
    }

    @Test
    @DisplayName("요구 조건의 추천 장소가 부족한 후보는 추천 생성에서 제외한다")
    void create_FiltersPlacesWithoutRequiredRecommendedPlaces() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place validPlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place invalidPlace = new Place("삼성역", new Point(127.2, 37.2));
        final Routes validRoutes = createRoutes(startPlace, validPlace, 10 * 60);
        final Courses validCourses = createCourses(startPlace, validPlace);

        final Recommendation recommendation = Recommendation.create(
                Map.of(
                        validPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다."),
                        invalidPlace, new RecommendationReason("#평균최소", "평균 이동 시간이 짧은 후보입니다.")
                ),
                Map.of(validPlace, createRecommendedPlaces(RecommendCondition.CAFE)),
                Map.of(validPlace, validRoutes),
                Map.of(validPlace, validCourses),
                Map.of(validPlace, List.of(CandidateSelectionTag.FAIRNESS)),
                0,
                List.of(RecommendCondition.CAFE)
        );

        assertThat(recommendation.size()).isEqualTo(1);
        assertThat(recommendation.get(0).getDestination()).isEqualTo(validPlace);
    }

    private Candidate createCandidate(int path1TravelTime, int path2TravelTime) {
        return createCandidate(path1TravelTime, path2TravelTime, CandidateSelectionTag.GENERAL);
    }

    private Candidate createCandidate(
            final int path1TravelTime,
            final int path2TravelTime,
            final CandidateSelectionTag tag
    ) {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, path1TravelTime, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, path2TravelTime, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final Routes routes = new Routes(List.of(route));

        final List<Point> points = List.of(startPlace.getPoint(), intermediatePlace.getPoint(), endPlace.getPoint());
        final Course course = new Course(points);
        final Courses courses = new Courses(List.of(course));

        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", new Point(127.2, 37.21), "카페", 5, "url","imageUrl");
        Map<RecommendCondition, List<RecommendedPlace>> categorizedRecommendedPlace = Map.of(RecommendCondition.CAFE, List.of(recommendedPlace));
        final CategorizedRecommendedPlaces recommendedPlaces = new CategorizedRecommendedPlaces(categorizedRecommendedPlace);
        return new Candidate(endPlace, routes, courses, recommendedPlaces, tag, "123", "123", 0);
    }

    private Routes createRoutes(final Place startPlace, final Place endPlace, final int travelTime) {
        return new Routes(List.of(new Route(List.of(new Path(
                startPlace,
                endPlace,
                TravelMethod.SUBWAY,
                travelTime,
                SubwayLine.fromTitle("2호선")
        )))));
    }

    private Courses createCourses(final Place startPlace, final Place endPlace) {
        return new Courses(List.of(new Course(List.of(startPlace.getPoint(), endPlace.getPoint()))));
    }

    private CategorizedRecommendedPlaces createRecommendedPlaces(final RecommendCondition recommendCondition) {
        final RecommendedPlace recommendedPlace = new RecommendedPlace(
                "스타벅스",
                new Point(127.2, 37.21),
                "카페",
                5,
                "url",
                "imageUrl"
        );
        return new CategorizedRecommendedPlaces(Map.of(recommendCondition, List.of(recommendedPlace)));
    }

}
