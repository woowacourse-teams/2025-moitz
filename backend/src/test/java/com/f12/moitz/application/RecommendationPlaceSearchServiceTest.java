package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.TravelMethod;
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
    @DisplayName("장소 검색 결과가 조건을 만족하면 최종 후보를 함께 반환한다")
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
        final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces = Map.of(
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
        assertThat(result.getRecommendedPlaces()).isEqualTo(recommendedPlaces);
        assertThat(result.getSelectedCandidates().getSelectedPlaces()).containsExactly(seolleung, samsung);

        final ArgumentCaptor<List<Place>> captor = ArgumentCaptor.forClass(List.class);
        verify(placeRecommender).recommendPlaces(captor.capture(), anyList());
        assertThat(captor.getValue()).containsExactly(seolleung, samsung);
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

    private CategorizedRecommendedPlaces createRecommendedPlaces(final String name) {
        return new CategorizedRecommendedPlaces(Map.of(
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
