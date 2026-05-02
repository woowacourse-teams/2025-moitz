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
    @DisplayName("최종 후보는 역 기준으로 중복 제거하고 선택된 후보가 속한 태그를 모두 부여한다")
    void select_AssignsAllMatchedTagsWithoutDuplicatingPlaces() {
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
                .containsEntry(shared.getPlace(), List.of(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.EFFICIENCY))
                .containsEntry(maxBurden.getPlace(), List.of(CandidateSelectionTag.MAX_BURDEN_RELIEF))
                .containsEntry(efficiency.getPlace(), List.of(CandidateSelectionTag.EFFICIENCY))
                .containsEntry(transfer.getPlace(), List.of(CandidateSelectionTag.TRANSFER))
                .containsEntry(general.getPlace(), List.of(CandidateSelectionTag.GENERAL));
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
        assertThat(selectionResult.getTags(nextFairness.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("최소 환승 태그는 최종 후보 중 환승 점수가 가장 낮은 후보에만 부여한다")
    void select_AssignsTransferTagOnlyToBestTransferScoresAmongFinalPlaces() {
        final RouteCandidate wangsimni = routeCandidateWithTransfers("왕십리역", List.of(0, 0, 2, 2));
        final RouteCandidate oksu = routeCandidateWithTransfers("옥수역", List.of(0, 1, 0, 1));
        final RouteCandidate yaksu = routeCandidateWithTransfers("약수역", List.of(1, 1, 0, 1));
        final RouteCandidate chungmuro = routeCandidateWithTransfers("충무로역", List.of(2, 1, 0, 1));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(wangsimni));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of(oksu));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(wangsimni, yaksu));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(wangsimni, yaksu, oksu, chungmuro));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(chungmuro));
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(wangsimni, oksu, yaksu, chungmuro),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                4,
                false,
                tagSelections
        );

        final FinalCandidateSelectionResult selectionResult = finalCandidateSelector.select(
                candidateSelectionResult,
                candidateSelectionResult.getSelectedPlaces(),
                ignored -> true,
                4
        );

        assertThat(selectionResult.getTags(wangsimni.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.EFFICIENCY);
        assertThat(selectionResult.getTags(oksu.getPlace()))
                .containsExactly(CandidateSelectionTag.MAX_BURDEN_RELIEF, CandidateSelectionTag.TRANSFER);
        assertThat(selectionResult.getTags(yaksu.getPlace()))
                .containsExactly(CandidateSelectionTag.EFFICIENCY);
        assertThat(selectionResult.getTags(chungmuro.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("최종 후보 중 같은 최소 환승 점수인 후보에는 최소 환승 태그를 함께 부여한다")
    void select_AddsTransferTagToAllBestTransferScorePlaces() {
        final RouteCandidate fairness = routeCandidateWithTransfers("공평후보역", List.of(0, 1));
        final RouteCandidate general = routeCandidateWithTransfers("일반후보역", List.of(0, 1));
        final RouteCandidate highTransfer = routeCandidateWithTransfers("환승많은역", List.of(1, 1));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of(highTransfer));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of());
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(fairness, general, highTransfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(fairness, highTransfer, general),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                3,
                false,
                tagSelections
        );

        final FinalCandidateSelectionResult selectionResult = finalCandidateSelector.select(
                candidateSelectionResult,
                candidateSelectionResult.getSelectedPlaces(),
                ignored -> true,
                3
        );

        assertThat(selectionResult.getTags(fairness.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.TRANSFER);
        assertThat(selectionResult.getTags(general.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
        assertThat(selectionResult.getTags(highTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.MAX_BURDEN_RELIEF);
    }

    @Test
    @DisplayName("최소 환승 후보가 기존 태그 수집에서 누락되어도 최종 태그 정규화에서 최소 환승 태그를 부여한다")
    void select_AddsTransferTagWhenBestTransferPlaceWasNotCollectedAsTransferTag() {
        final RouteCandidate bestTransfer = routeCandidateWithTransfers("최소환승역", List.of(0, 1));
        final RouteCandidate taggedTransfer = routeCandidateWithTransfers("태그수집환승역", List.of(1, 1));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(bestTransfer));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of());
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of());
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(taggedTransfer, bestTransfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of());
        final CandidateSelectionResult candidateSelectionResult = new CandidateSelectionResult(
                List.of(bestTransfer, taggedTransfer),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                tagSelections
        );

        final FinalCandidateSelectionResult selectionResult = finalCandidateSelector.select(
                candidateSelectionResult,
                candidateSelectionResult.getSelectedPlaces(),
                ignored -> true,
                2
        );

        assertThat(selectionResult.getTags(bestTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.TRANSFER);
        assertThat(selectionResult.getTags(taggedTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
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
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(shared, general));
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

    private RouteCandidate routeCandidateWithTransfers(final String name, final List<Integer> transferCounts) {
        final Place end = place(name);
        final List<Route> routes = java.util.stream.IntStream.range(0, transferCounts.size())
                .mapToObj(index -> routeWithTransfers(
                        new Place("출발" + index + "역", new Point(127.0 + index, 37.0 + index)),
                        end,
                        transferCounts.get(index)
                ))
                .toList();
        return new RouteCandidate(end, new Routes(routes));
    }

    private Route routeWithTransfers(final Place start, final Place end, final int transferCount) {
        final List<Path> paths = new java.util.ArrayList<>();
        Place currentStart = start;
        for (int index = 0; index < transferCount; index++) {
            final Place transferStation = new Place(
                    start.getName() + "환승" + index,
                    new Point(127.4 + index, 37.4 + index)
            );
            paths.add(new Path(currentStart, transferStation, TravelMethod.SUBWAY, 0, SubwayLine.fromTitle("2호선")));
            paths.add(new Path(transferStation, transferStation, TravelMethod.TRANSFER, 0, null));
            currentStart = transferStation;
        }
        paths.add(new Path(currentStart, end, TravelMethod.SUBWAY, 20 * 60, SubwayLine.fromTitle("3호선")));
        return new Route(paths);
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
