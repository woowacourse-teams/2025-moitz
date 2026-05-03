package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidateSelectionTest {

    @Test
    @DisplayName("태그별 후보를 순차적으로 섞어 검색 대상 후보를 만든다")
    void create_SelectsSearchCandidatesByRotatingTagSelections() {
        final RouteCandidate fairness1 = routeCandidate("공평1역");
        final RouteCandidate fairness2 = routeCandidate("공평2역");
        final RouteCandidate efficiency1 = routeCandidate("평균1역");
        final RouteCandidate transfer1 = routeCandidate("환승1역");
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness1, fairness2));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(efficiency1));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(transfer1));

        final CandidateSelection candidateSelection = CandidateSelection.create(
                tagSelections,
                List.of(),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                4,
                false,
                4
        );

        assertThat(candidateSelection.getSearchCandidatePlaces())
                .extracting(Place::getName)
                .containsExactly("공평1역", "평균1역", "환승1역", "공평2역");
    }

    @Test
    @DisplayName("태그 후보가 부족하면 기본 후보로 채우고 장소명 기준으로 중복을 제거한다")
    void create_FillsWithFallbackCandidatesWithoutDuplicatingPlaceNames() {
        final RouteCandidate selected = routeCandidate("선택역");
        final RouteCandidate duplicated = routeCandidate("선택역");
        final RouteCandidate fallback1 = routeCandidate("보충1역");
        final RouteCandidate fallback2 = routeCandidate("보충2역");
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(selected));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(duplicated));

        final CandidateSelection candidateSelection = CandidateSelection.create(
                tagSelections,
                List.of(duplicated, fallback1, fallback2),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                3,
                false,
                3
        );

        assertThat(candidateSelection.getSearchCandidatePlaces())
                .extracting(Place::getName)
                .containsExactly("선택역", "보충1역", "보충2역");
    }

    private RouteCandidate routeCandidate(final String name) {
        final Place start = new Place("출발역", new Point(127.0, 37.0));
        final Place end = new Place(name, new Point(127.1, 37.1));
        return new RouteCandidate(
                end,
                new Routes(List.of(new Route(List.of(Path.subway(
                        start,
                        end,
                        10 * 60,
                        SubwayLine.fromTitle("2호선")
                )))))
        );
    }

}
