package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private LocationRecommender locationRecommender;

    @Mock
    private PlaceRecommender placeRecommender;

    @Mock
    private PlaceFinder placeFinder;

    @Mock
    private RouteFinder routeFinder;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Test
    @DisplayName("모든 경로 탐색 후, 기준을 벗어나는 장소는 제외하고 올바르게 추천한다")
    void recommendLocationWithFiltering() {
        // Given
        final RecommendationRequest request = new RecommendationRequest(List.of("강남역", "역삼역"), "CHAT");
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place yeoksam = new Place("역삼역", new Point(127.036, 37.501));
        final List<Place> startingPlaces = List.of(gangnam, yeoksam);

        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final Place samsung = new Place("삼성역", new Point(127.063, 37.508));

        given(placeFinder.findPlacesByNames(anyList())).willReturn(startingPlaces);
        final RecommendedLocationResponse mockLocationResponse = new RecommendedLocationResponse(List.of(
                new LocationNameAndReason("선릉역", "이유1"),
                new LocationNameAndReason("삼성역", "이유2")
        ));

        given(locationRecommender.getRecommendedLocations(anyList(), anyString())).willReturn(mockLocationResponse);
        final Map<Place, String> generatedPlacesWithReason = Map.of(
                seolleung, "이유1",
                samsung, "이유2"
        );

        given(locationRecommender.recommendLocations(any())).willReturn(generatedPlacesWithReason);
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

        RecommendationsResponse expectedResponse = createExpectedResponse(startingPlaces, seolleung);
        given(recommendationMapper.toResponse(anyList(), any(Recommendation.class), any(Map.class))).willReturn(expectedResponse);


        // When
        RecommendationsResponse actualResponse = recommendationService.recommendLocation(request);


        // Then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.recommendedLocations()).hasSize(1);
        assertThat(actualResponse.recommendedLocations().getFirst().name()).isEqualTo("선릉역");

        verify(routeFinder, times(1)).findRoutes(anyList());
    }

    private RecommendationsResponse createExpectedResponse(List<Place> startingPlaces, Place recommendedPlace) {
        return new RecommendationsResponse(
                List.of(
                        new StartingPlaceResponse(1L, 1, startingPlaces.get(0).getPoint().getX(), startingPlaces.get(0).getPoint().getY(), startingPlaces.get(0).getName()),
                        new StartingPlaceResponse(2L, 2, startingPlaces.get(1).getPoint().getX(), startingPlaces.get(1).getPoint().getY(), startingPlaces.get(1).getName())
                ),
                List.of(
                        new RecommendationResponse(1L, 1, recommendedPlace.getPoint().getY(), recommendedPlace.getPoint().getX(), recommendedPlace.getName(), 10, true, "설명", "이유1", Collections.emptyList(), Collections.emptyList())
                )
        );
    }
}
