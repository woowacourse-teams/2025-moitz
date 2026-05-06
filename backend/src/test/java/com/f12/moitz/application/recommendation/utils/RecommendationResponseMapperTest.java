package com.f12.moitz.application.recommendation.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.dto.recommendation.LocationResponse;
import com.f12.moitz.application.dto.recommendation.RecommendationResultResponse;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.recommendation.Candidate;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.Recommendation;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Courses;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationResponseMapperTest {

    private final RecommendationResponseMapper mapper = new RecommendationResponseMapper();

    @Test
    @DisplayName("저장된 추천 이유가 오래된 다중 태그 기준이어도 정규화된 태그 기준으로 응답한다")
    void toResponse_RecomputesReasonWithNormalizedTags() {
        final Place startPlace = new Place("강남역", new Point(127.027, 37.497));
        final Place candidatePlace = new Place("선릉역", new Point(127.048, 37.504));
        final Candidate candidate = new Candidate(
                candidatePlace,
                new Routes(List.of(createRoute(startPlace, candidatePlace))),
                new Courses(List.of(new Course(List.of(startPlace.getPoint(), candidatePlace.getPoint())))),
                createRecommendedPlaces(),
                List.of(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.TRANSFER),
                "#가장공평 #최소환승",
                "선릉역은 모든 참여자의 이동 시간이 가장 공평한 기준, 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.",
                0
        );
        final Result result = new Result(
                List.of(RecommendCondition.CAFE),
                List.of(startPlace),
                new Recommendation(List.of(candidate))
        );

        final RecommendationResultResponse response = mapper.toResponse(result);

        final LocationResponse location = response.locations().getFirst();
        assertThat(location.tags()).containsExactly(CandidateSelectionTag.FAIRNESS.name());
        assertThat(location.tagInfo()).isEqualTo("모든 참여자의 이동 시간이 가장 공평한 기준");
        assertThat(location.description()).isEqualTo("#가장공평");
        assertThat(location.reason())
                .isEqualTo("선릉역은 모든 참여자의 이동 시간이 가장 공평한 기준을 반영해 추천된 만남 장소입니다.");
        assertThat(location.locationInfo()).isEqualTo(location.reason());
    }

    private Route createRoute(final Place startPlace, final Place candidatePlace) {
        return new Route(List.of(new Path(
                startPlace,
                candidatePlace,
                TravelMethod.SUBWAY,
                10 * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

    private RecommendedPlaces createRecommendedPlaces() {
        return new RecommendedPlaces(Map.of(
                RecommendCondition.CAFE,
                List.of(new RecommendedPlace(
                        "스타벅스 선릉점",
                        new Point(127.048, 37.504),
                        "카페",
                        5,
                        "url",
                        "imageUrl"
                ))
        ));
    }

}
