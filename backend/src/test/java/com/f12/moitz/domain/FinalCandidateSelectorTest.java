package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FinalCandidateSelectorTest {

    private final FinalCandidateSelector finalCandidateSelector = new FinalCandidateSelector();

    @Test
    @DisplayName("최종 후보는 태그 순서대로 선택하고 이미 선택된 후보는 다음 태그가 가져가지 않는다")
    void select_AssignsCandidatesByTagOrderWithoutDuplication() {
        final RouteCandidate shared = routeCandidate("공통후보역");
        final RouteCandidate maxBurden = routeCandidate("최장후보역");
        final RouteCandidate efficiency = routeCandidate("평균후보역");
        final RouteCandidate transfer = routeCandidate("환승후보역");
        final RouteCandidate general = routeCandidate("일반후보역");
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(shared, maxBurden, efficiency, transfer, general),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                createTagSelections(shared, maxBurden, efficiency, transfer, general)
        );

        final FinalCandidateSelectionResult selectionResult = finalCandidateSelector.select(
                candidateSelectionResult,
                candidateSelectionResult.getSelectedPlaces(),
                ignored -> true,
                5
        );

        assertThat(selectionResult.getSelectedPlaces())
                .extracting(Place::getName)
                .containsExactly("공통후보역", "최장후보역", "평균후보역", "환승후보역", "일반후보역");
        assertThat(selectionResult.getTagsByPlace())
                .containsEntry(shared.getPlace(), CandidateSelectionTag.FAIRNESS)
                .containsEntry(maxBurden.getPlace(), CandidateSelectionTag.MAX_BURDEN_RELIEF)
                .containsEntry(efficiency.getPlace(), CandidateSelectionTag.EFFICIENCY)
                .containsEntry(transfer.getPlace(), CandidateSelectionTag.TRANSFER)
                .containsEntry(general.getPlace(), CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("태그 후보가 조건을 만족하지 못하면 같은 태그의 다음 후보를 선택한다")
    void select_UsesNextCandidateWhenFirstTaggedCandidateDoesNotSatisfyCondition() {
        final RouteCandidate failedFairness = routeCandidate("실패후보역");
        final RouteCandidate nextFairness = routeCandidate("대체후보역");
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(failedFairness, nextFairness),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                Map.of(CandidateSelectionTag.FAIRNESS, List.of(failedFairness, nextFairness))
        );

        final FinalCandidateSelectionResult selectionResult = finalCandidateSelector.select(
                candidateSelectionResult,
                candidateSelectionResult.getSelectedPlaces(),
                place -> !place.getName().equals("실패후보역"),
                1
        );

        assertThat(selectionResult.getSelectedPlaces())
                .extracting(Place::getName)
                .containsExactly("대체후보역");
        assertThat(selectionResult.getTag(nextFairness.getPlace()))
                .isEqualTo(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("다음 장소 검색 대상은 비어있는 태그 후보를 우선하고 이 후보가 남아있다면 일반 후보로 채우지 않는다")
    void selectNextSearchPlaces_PrioritizesMissingTagsWithoutGeneralFillWhenTagCandidatesRemain() {
        final RouteCandidate fairness = routeCandidate("공평후보역");
        final RouteCandidate efficiency = routeCandidate("평균후보역");
        final RouteCandidate general = routeCandidate("일반후보역");
        final RouteCandidate filler1 = routeCandidate("보충후보1역");
        final RouteCandidate filler2 = routeCandidate("보충후보2역");
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of());
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(efficiency));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of());
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(fairness, efficiency, general, filler1, filler2),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                tagSelections
        );

        final List<Place> nextSearchPlaces = finalCandidateSelector.selectNextSearchPlaces(
                candidateSelectionResult,
                List.of(fairness.getPlace()),
                ignored -> true,
                4
        );

        assertThat(nextSearchPlaces)
                .extracting(Place::getName)
                .containsExactly("평균후보역", "일반후보역");
    }

    @Test
    @DisplayName("비어있는 태그의 미검색 후보가 더 이상 없으면 일반 후보로 추천 개수를 보충한다")
    void selectNextSearchPlaces_FillsWithGeneralCandidatesWhenMissingTagCandidatesAreExhausted() {
        final RouteCandidate fairness = routeCandidate("공평후보역");
        final RouteCandidate efficiency = routeCandidate("평균후보역");
        final RouteCandidate general = routeCandidate("일반후보역");
        final RouteCandidate filler1 = routeCandidate("보충후보1역");
        final RouteCandidate filler2 = routeCandidate("보충후보2역");
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of());
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(efficiency));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of());
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(fairness, efficiency, general, filler1, filler2),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                tagSelections
        );

        final List<Place> nextSearchPlaces = finalCandidateSelector.selectNextSearchPlaces(
                candidateSelectionResult,
                List.of(fairness.getPlace(), efficiency.getPlace(), general.getPlace()),
                ignored -> true,
                4
        );

        assertThat(nextSearchPlaces)
                .extracting(Place::getName)
                .containsExactly("보충후보1역", "보충후보2역");
    }

    private Map<CandidateSelectionTag, List<RouteCandidate>> createTagSelections(
            final RouteCandidate shared,
            final RouteCandidate maxBurden,
            final RouteCandidate efficiency,
            final RouteCandidate transfer,
            final RouteCandidate general
    ) {
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(shared));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of(maxBurden));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(shared, efficiency));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(transfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        return tagSelections;
    }

    private RouteCandidate routeCandidate(final String name) {
        final Place start = place("출발역");
        final Place end = place(name);
        return new RouteCandidate(
                end,
                new Routes(List.of(new Route(List.of(new Path(
                        start,
                        end,
                        TravelMethod.SUBWAY,
                        10 * 60,
                        SubwayLine.fromTitle("2호선")
                )))))
        );
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
