package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import java.util.List;
import java.util.Map;
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
    private PlaceService placeService;

    @Mock
    private RouteFinder routeFinder;

    @Mock
    private RecommendResultRepository recommendResultRepository;

    private RecommendationMapper recommendationMapper;

    @BeforeEach
    void setUp() {
        recommendationMapper = new RecommendationMapper();
        recommendationService = new RecommendationService(
                placeService,
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

        final RecommendationRequest request = new RecommendationRequest(List.of("강남역", "역삼역"), "CHAT");
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final List<Place> startingPlaces = List.of(gangnam, yeoksam);
        given(placeService.findByNames(anyList())).willReturn(startingPlaces);

        final RecommendedLocationsResponse mockLocationsResponse = new RecommendedLocationsResponse(List.of(
                new RecommendedLocationResponse("선릉역", "이유1", "설명1"),
                new RecommendedLocationResponse("삼성역", "이유2", "설명2")));
        given(locationRecommender.recommendLocations(anyList(), anyString())).willReturn(mockLocationsResponse);

        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final Place samsung = new Place("삼성역", new Point(127.063, 37.508));
        given(placeService.findByName("선릉역")).willReturn(seolleung);
        given(placeService.findByName("삼성역")).willReturn(samsung);

        Map<Place, List<RecommendedPlace>> mockRecommendedPlaces = Map.of(
                seolleung, List.of(new RecommendedPlace("스타벅스 선릉점", "카페", 5, "url")),
                samsung, List.of(new RecommendedPlace("스타벅스 삼성점", "카페", 4, "url"))
        );
        given(placeRecommender.recommendPlaces(anyList(), any(String.class))).willReturn(mockRecommendedPlaces);

        List<Route> mockRoutes = List.of(
                new Route(List.of(new Path(gangnam, seolleung, TravelMethod.SUBWAY, 10, "2호선"))),
                new Route(List.of(new Path(yeoksam, seolleung, TravelMethod.SUBWAY, 5, "2호선"))),
                new Route(List.of(new Path(gangnam, samsung, TravelMethod.SUBWAY, 999, "2호선"))),
                new Route(List.of(new Path(yeoksam, samsung, TravelMethod.SUBWAY, 10, "2호선")))
        );
        given(routeFinder.findRoutes(anyList())).willReturn(mockRoutes);

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
