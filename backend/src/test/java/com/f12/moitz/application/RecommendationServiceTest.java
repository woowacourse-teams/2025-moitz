package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private LocationRecommender locationRecommender;

    @Mock
    private PlaceRecommender placeRecommender;

    @Mock
    private SubwayStationService subwayStationService;

    @Mock
    private RouteFinder routeFinder;

    @Mock
    private RecommendResultRepository recommendResultRepository;

    private RecommendationMapper recommendationMapper;

    @BeforeEach
    void setUp() {
        recommendationMapper = new RecommendationMapper();
        recommendationService = new RecommendationService(
                subwayStationService,
                placeRecommender,
                locationRecommender,
                routeFinder,
                recommendationMapper,
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

        final RecommendedLocationsResponse mockLocationsResponse = new RecommendedLocationsResponse(List.of(
                new RecommendedLocationResponse("선릉역", "이유1", "설명1"),
                new RecommendedLocationResponse("삼성역", "이유2", "설명2")));
        given(locationRecommender.recommendLocations(anyList(), anyList(), anyList())).willReturn(mockLocationsResponse);

        final SubwayStation seolleung = new SubwayStation("선릉역", new Point(127.048, 37.504));
        final SubwayStation samsung = new SubwayStation("삼성역", new Point(127.063, 37.508));
        given(subwayStationService.getByName("선릉역")).willReturn(seolleung);
        given(subwayStationService.getByName("삼성역")).willReturn(samsung);

        Map<Place, CategorizedRecommendedPlaces> mockRecommendedPlaces = Map.of(
                seolleung, new CategorizedRecommendedPlaces(
                        Map.of(
                                RecommendCondition.CAFE, List.of(
                                        new RecommendedPlace(
                                                "스타벅스 선릉점",
                                                new Point(127.048, 37.504),
                                                "카페",
                                                5,
                                                "url"
                                        )
                                )
                        )
                ),
                samsung, new CategorizedRecommendedPlaces(
                        Map.of(
                                RecommendCondition.CAFE, List.of(
                                        new RecommendedPlace(
                                                "스타벅스 삼성점",
                                                new Point(127.063, 37.508),
                                                "카페",
                                                4,
                                                "url"
                                        )
                                )
                        )
                )
        );
        given(placeRecommender.recommendPlaces(anyList(), anyList())).willReturn(mockRecommendedPlaces);

        List<Route> mockRoutes = List.of(
                new Route(List.of(new Path(gangnam, seolleung, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(yeoksam, seolleung, TravelMethod.SUBWAY, 5, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(gangnam, samsung, TravelMethod.SUBWAY, 999, SubwayLine.fromTitle("2호선")))),
                new Route(List.of(new Path(yeoksam, samsung, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle("2호선"))))
        );
        given(routeFinder.findRoutes(anyList())).willReturn(mockRoutes);

        List<Course> mockCourses = List.of(
                new Course(List.of(gangnam, seolleung)),
                new Course(List.of(yeoksam, seolleung)),
                new Course(List.of(gangnam, samsung)),
                new Course(List.of(yeoksam, samsung))
        );
        given(routeFinder.findCourses(anyList())).willReturn(mockCourses);

        given(recommendResultRepository.saveAndReturnId(any(Result.class))).willReturn(new ObjectId());

        // When
        String resultId = recommendationService.recommendLocation(request);

        // Then
        assertThat(resultId).isNotNull();

        ArgumentCaptor<Result> resultCaptor = ArgumentCaptor.forClass(Result.class);
        verify(recommendResultRepository, times(1)).saveAndReturnId(resultCaptor.capture());

        Result savedResult = resultCaptor.getValue();
        assertThat(savedResult.getRecommendedLocationsCount()).isEqualTo(2);
        assertThat(savedResult.getRecommendedLocations().getCandidates().stream()
                .map(recommendedLocation -> recommendedLocation.getDestination().getName())
                .collect(Collectors.toList()))
                .containsExactlyInAnyOrder("선릉역", "삼성역");
    }

}
