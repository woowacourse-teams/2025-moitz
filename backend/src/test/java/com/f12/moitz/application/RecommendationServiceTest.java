package com.f12.moitz.application;

import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.application.port.Recommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private Recommender recommender;

    @Mock
    private RouteFinder routeFinder;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Test
    @DisplayName("장소를 추천하고, 반환된 결과값이 올바른지 검증한다")
    void recommendLocationAndVerify() {
        // Given
        final RecommendationRequest request = new RecommendationRequest(List.of("강남역", "역삼역"), "CHAT");

        final Place startingPlace1 = new Place("강남역", new Point(127.027, 37.497));
        final Place startingPlace2 = new Place("역삼역", new Point(127.036, 37.501));
        final List<Place> startingPlaces = List.of(startingPlace1, startingPlace2);
        final Place generatedPlace = new Place("선릉역", new Point(127.048, 37.504));
        final Map<Place, String> generatedPlacesWithReason = Map.of(generatedPlace, "이유");
        final Route route = new Route(List.of(new Path(startingPlace1, generatedPlace, TravelMethod.SUBWAY, 10, "2호선")));
        final RecommendedPlace recommendedPlace = new RecommendedPlace("스타벅스", "카페", 5, "url");

        given(recommender.findPlacesByNames(anyList())).willReturn(startingPlaces);
        RecommendedLocationResponse mock = Mockito.mock(RecommendedLocationResponse.class);
        given(recommender.getRecommendedLocations(anyList(), anyString())).willReturn(mock);
        given(recommender.recommendLocations(any(RecommendedLocationResponse.class))).willReturn(generatedPlacesWithReason);
        given(routeFinder.findRoute(any(Place.class), any(Place.class))).willReturn(route);
        given(recommender.recommendPlaces(anyList(), anyString())).willReturn(Map.of(generatedPlace, List.of(recommendedPlace)));

        final RecommendationsResponse expectedResponse = new RecommendationsResponse(
                List.of(
                        new StartingPlaceResponse(
                                1L,
                                1,
                                startingPlace1.getPoint().getX(),
                                startingPlace1.getPoint().getY(),
                                startingPlace1.getName()
                        ),
                        new StartingPlaceResponse(
                                2L,
                                2,
                                startingPlace2.getPoint().getX(),
                                startingPlace2.getPoint().getY(),
                                startingPlace2.getName()
                        )
                ),
                List.of(
                        new RecommendationResponse(
                                1L,
                                1,
                                37.504,
                                127.048,
                                "선릉역",
                                10,
                                true,
                                "설명",
                                "이유",
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                )
        );
        given(recommendationMapper.toResponse(anyList(), any(Recommendation.class), anyMap())).willReturn(expectedResponse);


        // When
        RecommendationsResponse actualResponse = recommendationService.recommendLocation(request);


        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actualResponse).isNotNull();
            softAssertions.assertThat(actualResponse.recommendedLocations()).isEqualTo(expectedResponse.recommendedLocations());
            softAssertions.assertThat(actualResponse.startingPlaces()).isEqualTo(expectedResponse.startingPlaces());
        });

        verify(routeFinder, times(2)).findRoute(any(Place.class), any(Place.class));
    }
}
