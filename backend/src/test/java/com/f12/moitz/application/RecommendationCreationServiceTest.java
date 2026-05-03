package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendationReason;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.RecommendedPlaces;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationCreationServiceTest {

    private final RecommendationCreationService service = new RecommendationCreationService();

    @Test
    @DisplayName("추천 생성에 필요한 정보를 조합해 추천 결과를 생성한다")
    void create() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final RecommendedPlaces recommendedPlaces = createRecommendedPlaces(RecommendCondition.CAFE);

        final Recommendation recommendation = service.create(
                Map.of(recommendedPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")),
                Map.of(recommendedPlace, recommendedPlaces),
                createRecommendedCandidateTravels(startPlace, recommendedPlace),
                createRecommendedCandidates(
                        List.of(recommendedPlace),
                        Map.of(recommendedPlace, List.of(CandidateSelectionTag.FAIRNESS))
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

        assertThatThrownBy(() -> service.create(
                Map.of(otherPlace, new RecommendationReason("#공평", "이동 시간이 고른 후보입니다.")),
                Map.of(recommendedPlace, createRecommendedPlaces(RecommendCondition.CAFE)),
                createRecommendedCandidateTravels(startPlace, recommendedPlace),
                createRecommendedCandidates(
                        List.of(recommendedPlace),
                        Map.of(recommendedPlace, List.of(CandidateSelectionTag.FAIRNESS))
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 이유가 누락되었습니다. 추천 지역: 선릉역");
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
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
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
