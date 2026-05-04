package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationPlaceSearchServiceTest {

    @Mock
    private PlaceRecommender placeRecommender;

    @Test
    @DisplayName("장소 검색 결과가 조건을 만족하면 추천 후보를 함께 반환한다")
    void search() {
        final RecommendationPlaceSearchService service = new RecommendationPlaceSearchService(placeRecommender);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final Place samsung = new Place("삼성역", new Point(127.063, 37.508));
        final RouteCandidate seolleungCandidate = new RouteCandidate(seolleung, createRoutes(gangnam, seolleung));
        final RouteCandidate samsungCandidate = new RouteCandidate(samsung, createRoutes(gangnam, samsung));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(seolleungCandidate, samsungCandidate),
                DispersionPolicy.TIER_1,
                DispersionPolicy.TIER_1,
                2,
                false,
                Map.of(
                        CandidateSelectionTag.FAIRNESS, List.of(seolleungCandidate),
                        CandidateSelectionTag.EFFICIENCY, List.of(samsungCandidate)
                )
        );
        final Map<Place, Routes> candidateRoutes = Map.of(
                seolleung, seolleungCandidate.getRoutes(),
                samsung, samsungCandidate.getRoutes()
        );
        final Map<Place, RecommendedPlaces> recommendedPlaces = Map.of(
                seolleung, createRecommendedPlaces("선릉 카페"),
                samsung, createRecommendedPlaces("삼성 카페")
        );
        given(placeRecommender.recommendPlaces(anyList(), anyList())).willReturn(recommendedPlaces);

        final RecommendationPlaceSearchResult result = service.search(
                candidateSelection,
                List.of(RecommendCondition.CAFE),
                candidateRoutes,
                5,
                2
        );

        assertThat(result.getSearchedPlaces()).containsExactly(seolleung, samsung);
        assertThat(result.getSearchedPlaceCount()).isEqualTo(2);
        assertThat(result.getRecommendedPlaces()).isEqualTo(recommendedPlaces);
        assertThat(result.getRecommendedPlaceCount()).isEqualTo(2);
        assertThat(result.getRecommendedCandidates().getPlaces()).containsExactly(seolleung, samsung);

        final ArgumentCaptor<List<Place>> captor = ArgumentCaptor.forClass(List.class);
        verify(placeRecommender).recommendPlaces(captor.capture(), anyList());
        assertThat(captor.getValue()).containsExactly(seolleung, samsung);
    }

    @Test
    @DisplayName("장소 검색 결과가 조건을 만족하지 못하면 추천 후보에서 제외한다")
    void search_FiltersPlacesWithoutRequiredRecommendedPlaces() {
        final RecommendationPlaceSearchService service = new RecommendationPlaceSearchService(placeRecommender);
        final Place gangnam = new Place("강남역", new Point(127.027, 37.497));
        final Place seolleung = new Place("선릉역", new Point(127.048, 37.504));
        final Place samsung = new Place("삼성역", new Point(127.063, 37.508));
        final RouteCandidate seolleungCandidate = new RouteCandidate(seolleung, createRoutes(gangnam, seolleung));
        final RouteCandidate samsungCandidate = new RouteCandidate(samsung, createRoutes(gangnam, samsung));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(seolleungCandidate, samsungCandidate),
                DispersionPolicy.TIER_1,
                DispersionPolicy.TIER_1,
                2,
                false,
                Map.of(
                        CandidateSelectionTag.FAIRNESS, List.of(seolleungCandidate),
                        CandidateSelectionTag.EFFICIENCY, List.of(samsungCandidate)
                )
        );
        final Map<Place, Routes> candidateRoutes = Map.of(
                seolleung, seolleungCandidate.getRoutes(),
                samsung, samsungCandidate.getRoutes()
        );
        given(placeRecommender.recommendPlaces(anyList(), anyList())).willReturn(Map.of(
                seolleung, createRecommendedPlaces("선릉 카페"),
                samsung, new RecommendedPlaces(Map.of())
        ));

        final RecommendationPlaceSearchResult result = service.search(
                candidateSelection,
                List.of(RecommendCondition.CAFE),
                candidateRoutes,
                2,
                2
        );

        assertThat(result.getRecommendedCandidates().getPlaces()).containsExactly(seolleung);
    }

    private Routes createRoutes(final Place origin, final Place destination) {
        return new Routes(List.of(new Route(List.of(new Path(
                origin,
                destination,
                TravelMethod.SUBWAY,
                10 * 60,
                SubwayLine.fromTitle("2호선")
        )))));
    }

    private RecommendedPlaces createRecommendedPlaces(final String name) {
        return new RecommendedPlaces(Map.of(
                RecommendCondition.CAFE,
                List.of(new RecommendedPlace(
                        name,
                        new Point(127.048, 37.504),
                        "카페",
                        5,
                        "url",
                        "imageUrl"
                ))
        ));
    }

}
