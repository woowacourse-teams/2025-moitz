package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.RecommendedPlaces;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedCandidateConfirmationServiceTest {

    private final RecommendedCandidateRouteService recommendedCandidateRouteService =
            mock(RecommendedCandidateRouteService.class);
    private final RecommendedCandidateConfirmationService service =
            new RecommendedCandidateConfirmationService(recommendedCandidateRouteService);

    @Test
    @DisplayName("추천 후보를 확정하고 후보 이동 정보를 준비한다")
    void confirm() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place recommendedPlace = new Place("선릉역", new Point(127.1, 37.1));
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(recommendedPlace),
                Map.of(recommendedPlace, List.of(CandidateSelectionTag.FAIRNESS))
        );
        final RecommendedCandidateTravels recommendedCandidateTravels =
                createRecommendedCandidateTravels(startPlace, recommendedPlace);
        final RecommendationPlaceSearchResult recommendationPlaceSearchResult = new RecommendationPlaceSearchResult(
                List.of(recommendedPlace),
                Map.of(recommendedPlace, createRecommendedPlaces()),
                recommendedCandidates
        );
        final RouteOrigins routeOrigins = mock(RouteOrigins.class);
        final Map<Place, Routes> candidateRoutes = Map.of(
                recommendedPlace,
                createRoutes(startPlace, recommendedPlace, 10 * 60)
        );
        given(recommendedCandidateRouteService.prepare(routeOrigins, recommendedCandidates, candidateRoutes))
                .willReturn(recommendedCandidateTravels);

        final RecommendedCandidateConfirmationResult result = service.confirm(
                List.of(recommendedPlace),
                recommendationPlaceSearchResult,
                routeOrigins,
                candidateRoutes,
                List.of(RecommendCondition.CAFE)
        );

        assertThat(result.getRecommendedCandidates()).isEqualTo(recommendedCandidates);
        assertThat(result.getRecommendedCandidateTravels()).isEqualTo(recommendedCandidateTravels);
    }

    @Test
    @DisplayName("추천 후보가 없으면 제어된 예외를 반환한다")
    void confirm_ThrowsExceptionWhenRecommendedCandidatesAreEmpty() {
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(List.of(), Map.of());
        final RecommendationPlaceSearchResult recommendationPlaceSearchResult = new RecommendationPlaceSearchResult(
                List.of(),
                Map.of(),
                recommendedCandidates
        );

        assertThatThrownBy(() -> service.confirm(
                List.of(),
                recommendationPlaceSearchResult,
                mock(RouteOrigins.class),
                Map.of(),
                List.of(RecommendCondition.CAFE)
        ))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.RECOMMENDATION_NOT_FOUND));
        verify(recommendedCandidateRouteService, never()).prepare(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
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

    private RecommendedPlaces createRecommendedPlaces() {
        final RecommendedPlace recommendedPlace = new RecommendedPlace(
                "스타벅스",
                new Point(127.2, 37.21),
                "카페",
                5,
                "url",
                "imageUrl"
        );
        return new RecommendedPlaces(Map.of(RecommendCondition.CAFE, List.of(recommendedPlace)));
    }

}
