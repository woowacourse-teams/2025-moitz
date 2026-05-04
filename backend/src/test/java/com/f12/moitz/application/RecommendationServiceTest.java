package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationResponseMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.RecommendedPlaces;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private RecommendationService recommendationService;

    @Mock
    private LocationReasonGenerator locationReasonGenerator;

    @Mock
    private PlaceRecommender placeRecommender;

    @Mock
    private SubwayStationService subwayStationService;

    @Mock
    private RouteFinder routeFinder;

    @Mock
    private RecommendResultRepository recommendResultRepository;

    private RecommendationResponseMapper recommendationResponseMapper;

    @BeforeEach
    void setUp() {
        recommendationResponseMapper = new RecommendationResponseMapper();
        recommendationService = new RecommendationService(
                new RouteOriginPreparationService(subwayStationService),
                new RecommendedCandidateReasonService(locationReasonGenerator),
                new RouteOriginDispersionService(routeFinder),
                new RouteCandidatePreparationService(subwayStationService, routeFinder),
                new RecommendationPlaceSearchService(placeRecommender),
                new RecommendedCandidateRouteService(routeFinder),
                recommendationResponseMapper,
                recommendResultRepository
        );
    }

    @Test
    @DisplayName("추천 요청 시 올바른 최종 결과를 저장해야 한다")
    void recommendLocation_Success() {
        // Given
        final RecommendationRequest request = new RecommendationRequest(List.of("강남역", "역삼역"), List.of("CAFE"));
        final SubwayStation gangnam = new SubwayStation("강남역", new Point(127.027, 37.497));
        final SubwayStation yeoksam = new SubwayStation("역삼역", new Point(127.036, 37.501));
        given(subwayStationService.findByName("강남역")).willReturn(Optional.of(gangnam));
        given(subwayStationService.findByName("역삼역")).willReturn(Optional.of(yeoksam));

        final SubwayStation seolleung = new SubwayStation("선릉역", new Point(127.048, 37.504));
        final SubwayStation samsung = new SubwayStation("삼성역", new Point(127.063, 37.508));
        given(subwayStationService.generateCandidatePlace(anyList(), anyInt()))
                .willReturn(List.of(gangnam, yeoksam, seolleung, samsung));

        Map<Place, RecommendedPlaces> mockRecommendedPlaces = Map.of(
                seolleung, new RecommendedPlaces(
                        Map.of(
                                RecommendCondition.CAFE, List.of(
                                        new RecommendedPlace(
                                                "스타벅스 선릉점",
                                                new Point(127.048, 37.504),
                                                "카페",
                                                5,
                                                "url",
                                                "imageUrl"
                                        )
                                )
                        )
                ),
                samsung, new RecommendedPlaces(
                        Map.of(
                                RecommendCondition.CAFE, List.of(
                                        new RecommendedPlace(
                                                "스타벅스 삼성점",
                                                new Point(127.063, 37.508),
                                                "카페",
                                                4,
                                                "url",
                                                "imageUrl"
                                        )
                                )
                        )
                )
        );
        given(placeRecommender.recommendPlaces(anyList(), anyList()))
                .willReturn(mockRecommendedPlaces);

        List<Route> pairwiseRoutes = List.of(
                new Route(List.of(new Path(gangnam, yeoksam, TravelMethod.SUBWAY, 2 * 60, SubwayLine.fromTitle("2호선"))))
        );
        List<Route> mockRoutes = List.of(
                new Route(List.of(new Path(gangnam, seolleung, TravelMethod.SUBWAY, 10 * 60, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(yeoksam, seolleung, TravelMethod.SUBWAY, 5 * 60, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(gangnam, samsung, TravelMethod.SUBWAY, 14 * 60, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(yeoksam, samsung, TravelMethod.SUBWAY, 10 * 60, SubwayLine.fromTitle("2호선"))))
        );
        given(routeFinder.findRoutes(anyList())).willReturn(pairwiseRoutes, mockRoutes);

        List<CandidateRoute> mockCandidateRoutes = List.of(
                new CandidateRoute(mockRoutes.get(0), new Course(List.of(gangnam.getPoint(), seolleung.getPoint()))),
                new CandidateRoute(mockRoutes.get(1), new Course(List.of(yeoksam.getPoint(), seolleung.getPoint()))),
                new CandidateRoute(mockRoutes.get(2), new Course(List.of(gangnam.getPoint(), samsung.getPoint()))),
                new CandidateRoute(mockRoutes.get(3), new Course(List.of(yeoksam.getPoint(), samsung.getPoint())))
        );
        given(routeFinder.findCandidateRoutes(anyList())).willReturn(mockCandidateRoutes);
        given(locationReasonGenerator.generateReasons(anyList(), anyMap())).willReturn(Map.of(
                "선릉역", new ReasonAndDescription("설명1", "이유1"),
                "삼성역", new ReasonAndDescription("설명2", "이유2")
        ));

        given(recommendResultRepository.saveAndReturnId(any(Result.class))).willReturn(new ObjectId());

        // When
        String resultId = recommendationService.recommendLocation(request).id();

        // Then
        assertThat(resultId).isNotNull();

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(recommendResultRepository, times(1)).saveAndReturnId(resultCaptor.capture());

        Result savedResult = resultCaptor.getValue();
        assertThat(savedResult.getRecommendedLocationsCount()).isEqualTo(2);
        assertThat(IntStream.range(0, savedResult.getRecommendedLocationsCount())
                .mapToObj(index -> savedResult.getRecommendedLocations().get(index).getDestination().getName())
                .toList())
                .containsExactly("삼성역", "선릉역");
        assertThat(IntStream.range(0, savedResult.getRecommendedLocationsCount())
                .mapToObj(index -> savedResult.getRecommendedLocations().get(index).getTags().stream()
                        .map(Enum::name)
                        .toList())
                .toList())
                .containsExactly(
                        List.of("FAIRNESS", "EFFICIENCY", "TRANSFER"),
                        List.of("MAX_BURDEN_RELIEF", "EFFICIENCY", "TRANSFER")
                );
    }

    @Test
    @DisplayName("장소 조건을 만족하는 추천 후보가 없으면 제어된 예외를 반환한다")
    void recommendLocation_ThrowsBadRequestWhenNoRecommendationMatches() {
        final RecommendationRequest request = new RecommendationRequest(List.of("강남역", "역삼역"), List.of("CAFE"));
        final SubwayStation gangnam = new SubwayStation("강남역", new Point(127.027, 37.497));
        final SubwayStation yeoksam = new SubwayStation("역삼역", new Point(127.036, 37.501));
        final SubwayStation seolleung = new SubwayStation("선릉역", new Point(127.048, 37.504));

        given(subwayStationService.findByName("강남역")).willReturn(Optional.of(gangnam));
        given(subwayStationService.findByName("역삼역")).willReturn(Optional.of(yeoksam));
        given(subwayStationService.generateCandidatePlace(anyList(), anyInt()))
                .willReturn(List.of(gangnam, yeoksam, seolleung));

        given(routeFinder.findRoutes(anyList())).willReturn(
                List.of(new Route(List.of(new Path(gangnam, yeoksam, TravelMethod.SUBWAY, 2 * 60, SubwayLine.fromTitle("2호선"))))),
                List.of(
                new Route(List.of(new Path(gangnam, seolleung, TravelMethod.SUBWAY, 10 * 60, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(yeoksam, seolleung, TravelMethod.SUBWAY, 5 * 60, SubwayLine.fromTitle("2호선"))))
        ));
        given(placeRecommender.recommendPlaces(anyList(), anyList())).willReturn(Map.of(
                seolleung, new RecommendedPlaces(Map.of())
        ));

        assertThatThrownBy(() -> recommendationService.recommendLocation(request))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.RECOMMENDATION_NOT_FOUND));
    }

    @Test
    @DisplayName("출발지명이 같은 역으로 해석되면 제어된 예외를 반환한다")
    void recommendLocation_ThrowsBadRequestWhenResolvedStartingPlacesAreDuplicated() {
        final RecommendationRequest request = new RecommendationRequest(List.of("이수역", "총신대입구역"), List.of("CAFE"));
        final SubwayStation isu = new SubwayStation("이수역", new Point(126.982, 37.486));

        given(subwayStationService.findByName("이수역")).willReturn(Optional.of(isu));
        given(subwayStationService.findByName("총신대입구역")).willReturn(Optional.of(isu));

        assertThatThrownBy(() -> recommendationService.recommendLocation(request))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_START_LOCATION));
        verify(routeFinder, times(0)).findRoutes(anyList());
    }

}
