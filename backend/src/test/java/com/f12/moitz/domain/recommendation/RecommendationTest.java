package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Courses;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationTest {

    @Test
    @DisplayName("MongoDB 역직렬화를 위한 기본 생성자를 제공한다")
    void hasDefaultConstructorForMongoDeserialization() throws NoSuchMethodException {
        final java.lang.reflect.Constructor<Recommendation> constructor = Recommendation.class.getDeclaredConstructor();

        assertThat(Modifier.isProtected(constructor.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("추천 생성에 필요한 정보를 조합해 추천 결과를 생성한다")
    void create() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final RecommendedPlaces recommendedPlaces = createRecommendedPlaces(RecommendCondition.CAFE);

        final Recommendation recommendation = Recommendation.create(
                Map.of(recommendedPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")),
                Map.of(recommendedPlace, recommendedPlaces),
                createRecommendedCandidateTravels(startPlace, recommendedPlace),
                createRecommendedCandidates(
                        List.of(recommendedPlace),
                        Map.of(recommendedPlace, CandidateSelectionTag.FAIRNESS)
                )
        );

        assertThat(recommendation.size()).isEqualTo(1);
        assertThat(recommendation.get(0).getDestination()).isEqualTo(recommendedPlace);
        assertThat(recommendation.get(0).getTags()).containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendation.get(0).getDescription()).isEqualTo("#공평");
        assertThat(recommendation.get(0).getReason()).isEqualTo("이동 시간이 고른 후보입니다.");
        assertThat(recommendation.get(0).getVotes()).isZero();
    }

    @Test
    @DisplayName("추천 이유가 누락되면 추천 결과를 생성할 수 없다")
    void create_ThrowsExceptionWhenRecommendationReasonIsMissing() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place otherPlace = new Place("삼성역", new Point(127.2, 37.2));

        assertSoftly(softAssertions -> softAssertions.assertThatThrownBy(() -> Recommendation.create(
                Map.of(otherPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")),
                Map.of(recommendedPlace, createRecommendedPlaces(RecommendCondition.CAFE)),
                createRecommendedCandidateTravels(startPlace, recommendedPlace),
                createRecommendedCandidates(
                        List.of(recommendedPlace),
                        Map.of(recommendedPlace, CandidateSelectionTag.FAIRNESS)
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 이유가 누락되었습니다. 추천 지역: 선릉역"));
    }

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
        final List<Candidate> candidatesWithNull = Arrays.asList(createCandidate(20, 10), null);

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Recommendation(candidatesNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보지는 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Recommendation(candidatesEmpty))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보지는 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Recommendation(candidatesWithNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보지 목록에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("추천 후보 목록은 외부에서 변경할 수 없다")
    void constructor_CopiesCandidates() {
        final Candidate candidate = createCandidate(20, 10);
        final List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        final Recommendation recommendation = new Recommendation(candidates);

        candidates.clear();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendation.size()).isEqualTo(1);
            softAssertions.assertThat(recommendation.get(0)).isEqualTo(candidate);
        });
    }

    @Test
    @DisplayName("후보지들을 공평성 점수 우선으로 정렬한다")
    void sortCandidates() {
        // Given
        final Candidate generalCandidate = createCandidate(1200, 600, CandidateSelectionTag.GENERAL);
        final Candidate transferCandidate = createCandidate(1500, 600, CandidateSelectionTag.TRANSFER);
        final Candidate fairnessCandidate = createCandidate(1800, 600, CandidateSelectionTag.FAIRNESS);
        final List<Candidate> candidates = List.of(generalCandidate, transferCandidate, fairnessCandidate);

        // When
        final Recommendation recommendation = new Recommendation(candidates);

        // Then
        assertThat(recommendation.get(0)).isEqualTo(generalCandidate);
        assertThat(recommendation.get(1)).isEqualTo(transferCandidate);
        assertThat(recommendation.get(2)).isEqualTo(fairnessCandidate);
    }

    @Test
    @DisplayName("공평성 점수가 같으면 태그 순서로 정렬한다")
    void sortCandidates_UsesTagPriorityWhenFairnessScoreIsSame() {
        final Candidate generalCandidate = createCandidate(1200, 600, CandidateSelectionTag.GENERAL);
        final Candidate fairnessCandidate = createCandidate(1200, 600, CandidateSelectionTag.FAIRNESS);
        final List<Candidate> candidates = List.of(generalCandidate, fairnessCandidate);

        final Recommendation recommendation = new Recommendation(candidates);

        assertThat(recommendation.get(0)).isEqualTo(fairnessCandidate);
        assertThat(recommendation.get(1)).isEqualTo(generalCandidate);
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
        Map<RecommendCondition, List<RecommendedPlace>> recommendedPlacesByCondition = Map.of(RecommendCondition.CAFE, List.of(recommendedPlace));
        final RecommendedPlaces recommendedPlaces = new RecommendedPlaces(recommendedPlacesByCondition);
        return new Candidate(endPlace, routes, courses, recommendedPlaces, tag, "123", "123", 0);
    }

    private RecommendedCandidateTravels createRecommendedCandidateTravels(
            final Place startPlace,
            final Place recommendedPlace
    ) {
        return new RecommendedCandidateTravels(
                Map.of(recommendedPlace, createRoutes(startPlace, recommendedPlace, 10 * 60)),
                Map.of(recommendedPlace, createCourses(startPlace, recommendedPlace))
        );
    }

    private RecommendedCandidates createRecommendedCandidates(
            final List<Place> recommendedPlaces,
            final Map<Place, CandidateSelectionTag> tagsByPlace
    ) {
        return new RecommendedCandidates(recommendedPlaces, tagsByPlace);
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

    private RecommendedPlaces createRecommendedPlaces(final RecommendCondition recommendCondition) {
        final RecommendedPlace recommendedPlace = new RecommendedPlace(
                "스타벅스",
                new Point(127.2, 37.21),
                "카페",
                5,
                "url",
                "imageUrl"
        );
        return new RecommendedPlaces(Map.of(recommendCondition, List.of(recommendedPlace)));
    }

}
