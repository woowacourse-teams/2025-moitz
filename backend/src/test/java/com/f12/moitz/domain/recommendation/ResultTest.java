package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    @DisplayName("추천 결과는 필수 정보를 가져야 한다")
    void constructor_ThrowsExceptionWhenArgumentsAreInvalid() {
        final Recommendation recommendation = recommendation();
        final Place startingPlace = place("강남역");

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Result(null, List.of(startingPlace), recommendation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 조건은 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(List.of(), List.of(startingPlace), recommendation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 조건은 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(
                            Arrays.asList(RecommendCondition.CAFE, null),
                            List.of(startingPlace),
                            recommendation
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 조건에 null이 포함될 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(List.of(RecommendCondition.CAFE), null, recommendation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발지들은 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(List.of(RecommendCondition.CAFE), List.of(), recommendation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발지들은 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(
                            List.of(RecommendCondition.CAFE),
                            Arrays.asList(startingPlace, null),
                            recommendation
                    ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("출발지 목록에 null이 포함될 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Result(List.of(RecommendCondition.CAFE), List.of(startingPlace), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 정보는 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("추천 조건과 출발지 목록은 외부에서 변경할 수 없다")
    void constructor_CopiesLists() {
        final List<RecommendCondition> recommendConditions = new ArrayList<>();
        recommendConditions.add(RecommendCondition.CAFE);
        final List<Place> startingPlaces = new ArrayList<>();
        startingPlaces.add(place("강남역"));
        final Result result = new Result(recommendConditions, startingPlaces, recommendation());

        recommendConditions.clear();
        startingPlaces.clear();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getRecommendConditions()).containsExactly(RecommendCondition.CAFE);
            softAssertions.assertThat(result.getStartingPlaces()).hasSize(1);
            softAssertions.assertThatThrownBy(() -> result.getRecommendConditions().add(RecommendCondition.RESTAURANT))
                    .isInstanceOf(UnsupportedOperationException.class);
        });
    }

    @Test
    @DisplayName("식별자를 가진 추천 결과도 추천 조건과 출발지 목록을 방어 복사한다")
    void constructorWithId_CopiesLists() {
        final List<RecommendCondition> recommendConditions = new ArrayList<>();
        recommendConditions.add(RecommendCondition.CAFE);
        final List<Place> startingPlaces = new ArrayList<>();
        startingPlaces.add(place("강남역"));
        final ObjectId id = new ObjectId();

        final Result result = new Result(id, recommendConditions, startingPlaces, recommendation());

        recommendConditions.clear();
        startingPlaces.clear();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getId()).isEqualTo(id);
            softAssertions.assertThat(result.getRecommendConditions()).containsExactly(RecommendCondition.CAFE);
            softAssertions.assertThat(result.getStartingPlaces()).hasSize(1);
        });
    }

    private Recommendation recommendation() {
        final Place start = place("강남역");
        final Place destination = place("선릉역");
        final Route route = new Route(List.of(Path.subway(start, destination, 10, SubwayLine.fromTitle("2호선"))));
        final Course course = new Course(List.of(start.getPoint(), destination.getPoint()));
        final RecommendedPlace recommendedPlace = new RecommendedPlace(
                "카페",
                new Point(127.2, 37.2),
                "카페",
                5,
                "url",
                "imageUrl"
        );
        final Candidate candidate = Candidate.create(
                destination,
                new RecommendationReason("#공평", "이동 시간이 고르게 분산됩니다."),
                new RecommendedPlaces(java.util.Map.of(RecommendCondition.CAFE, List.of(recommendedPlace))),
                List.of(new CandidateRoute(route, course)),
                List.of(CandidateSelectionTag.FAIRNESS)
        );
        return new Recommendation(List.of(candidate));
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
