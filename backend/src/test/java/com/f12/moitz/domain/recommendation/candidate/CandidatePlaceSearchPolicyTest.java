package com.f12.moitz.domain.recommendation.candidate;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.route.TravelMethod;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidatePlaceSearchPolicyTest {

    private final CandidatePlaceSearchPolicy candidatePlaceSearchPolicy = new CandidatePlaceSearchPolicy();

    @Test
    @DisplayName("추천 후보는 태그별로 하나의 장소를 선택하고 이미 선택된 장소는 다음 태그에서 건너뛴다")
    void select_AssignsSinglePlacePerTagWithoutDuplicatingPlaces() {
        final RouteCandidate shared = routeCandidate("공통후보역");
        final RouteCandidate maxBurden = routeCandidate("최장후보역");
        final RouteCandidate efficiency = routeCandidate("평균후보역");
        final RouteCandidate transfer = routeCandidate("환승후보역");
        final RouteCandidate general = routeCandidate("일반후보역");
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(shared, maxBurden, efficiency, transfer, general),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                createTagSelections(shared, maxBurden, efficiency, transfer, general)
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                5
        );

        assertThat(recommendedCandidates.getPlaces())
                .extracting(Place::getName)
                .containsExactly("공통후보역", "최장후보역", "평균후보역", "환승후보역", "일반후보역");
        assertThat(recommendedCandidates.getTags(shared.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendedCandidates.getTags(maxBurden.getPlace()))
                .containsExactly(CandidateSelectionTag.MAX_BURDEN_RELIEF);
        assertThat(recommendedCandidates.getTags(efficiency.getPlace()))
                .containsExactly(CandidateSelectionTag.EFFICIENCY);
        assertThat(recommendedCandidates.getTags(transfer.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
        assertThat(recommendedCandidates.getTags(general.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("장소명이 같아도 좌표가 다르면 서로 다른 추천 후보로 선택한다")
    void select_DoesNotDeduplicateOnlyByPlaceName() {
        final RouteCandidate first = routeCandidate("동명장소", new Point(127.1, 37.1));
        final RouteCandidate second = routeCandidate("동명장소", new Point(127.2, 37.2));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(first));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(second));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(first, second),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                tagSelections
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                2
        );

        assertThat(recommendedCandidates.getPlaces())
                .containsExactly(first.getPlace(), second.getPlace());
    }

    @Test
    @DisplayName("태그 후보가 없어서 일반 태그로 보충할 때도 일반 태그는 하나의 장소에만 부여한다")
    void select_AssignsGeneralTagToSingleFallbackCandidateOnly() {
        final RouteCandidate first = routeCandidate("일반후보1역");
        final RouteCandidate second = routeCandidate("일반후보2역");
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(first, second),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                Map.of()
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                5
        );

        assertThat(recommendedCandidates.getPlaces())
                .containsExactly(first.getPlace());
        assertThat(recommendedCandidates.getTags(first.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("태그 후보가 조건을 만족하지 못하면 같은 태그의 다음 후보를 선택한다")
    void select_UsesNextCandidateWhenFirstTaggedCandidateDoesNotSatisfyCondition() {
        final RouteCandidate failedFairness = routeCandidate("실패후보역");
        final RouteCandidate nextFairness = routeCandidate("대체후보역");
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(failedFairness, nextFairness),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                Map.of(CandidateSelectionTag.FAIRNESS, List.of(failedFairness, nextFairness))
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                place -> !place.getName().equals("실패후보역"),
                1
        );

        assertThat(recommendedCandidates.getPlaces())
                .extracting(Place::getName)
                .containsExactly("대체후보역");
        assertThat(recommendedCandidates.getTag(nextFairness.getPlace()))
                .isEqualTo(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendedCandidates.getTags(nextFairness.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
    }

    @Test
    @DisplayName("이미 선택된 후보는 최소 환승 태그에 다시 매핑하지 않고 다음 후보를 선택한다")
    void select_SelectsNextTransferCandidateWhenBestTransferPlaceAlreadyHasAnotherTag() {
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
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(wangsimni, oksu, yaksu, chungmuro),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                4,
                false,
                tagSelections
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                4
        );

        assertThat(recommendedCandidates.getTags(wangsimni.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendedCandidates.getTags(oksu.getPlace()))
                .containsExactly(CandidateSelectionTag.MAX_BURDEN_RELIEF);
        assertThat(recommendedCandidates.getTags(yaksu.getPlace()))
                .containsExactly(CandidateSelectionTag.EFFICIENCY);
        assertThat(recommendedCandidates.getTags(chungmuro.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
    }

    @Test
    @DisplayName("최소 환승 점수가 같은 후보가 있어도 최소 환승 태그는 단일 후보에만 부여한다")
    void select_AssignsTransferTagToSingleCandidateOnly() {
        final RouteCandidate fairness = routeCandidateWithTransfers("공평후보역", List.of(0, 1));
        final RouteCandidate general = routeCandidateWithTransfers("일반후보역", List.of(0, 1));
        final RouteCandidate highTransfer = routeCandidateWithTransfers("환승많은역", List.of(1, 1));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of(highTransfer));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of());
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(fairness, general, highTransfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(fairness, highTransfer, general),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                3,
                false,
                tagSelections
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                3
        );

        assertThat(recommendedCandidates.getTags(fairness.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendedCandidates.getTags(general.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
        assertThat(recommendedCandidates.getTags(highTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.MAX_BURDEN_RELIEF);
    }

    @Test
    @DisplayName("최소 환승 태그는 사후 보정으로 다른 태그 후보에 추가하지 않는다")
    void select_DoesNotAddTransferTagByPostNormalization() {
        final RouteCandidate bestTransfer = routeCandidateWithTransfers("최소환승역", List.of(0, 1));
        final RouteCandidate taggedTransfer = routeCandidateWithTransfers("태그수집환승역", List.of(1, 1));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(bestTransfer));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of());
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of());
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(taggedTransfer, bestTransfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of());
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(bestTransfer, taggedTransfer),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                tagSelections
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                2
        );

        assertThat(recommendedCandidates.getTags(bestTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.FAIRNESS);
        assertThat(recommendedCandidates.getTags(taggedTransfer.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
    }

    @Test
    @DisplayName("장소명이 같아도 다른 장소에는 최소 환승 태그를 전파하지 않는다")
    void select_DoesNotPropagateTransferTagOnlyByPlaceName() {
        final RouteCandidate transfer = routeCandidateWithTransfers("동명장소", new Point(127.1, 37.1), List.of(0, 0));
        final RouteCandidate general = routeCandidateWithTransfers("동명장소", new Point(127.2, 37.2), List.of(0, 0));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of());
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of());
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of());
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(transfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(general));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(general, transfer),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                tagSelections
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                candidateSelection.getSearchCandidatePlaces(),
                ignored -> true,
                2
        );

        assertThat(recommendedCandidates.getTags(general.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
        assertThat(recommendedCandidates.getTags(transfer.getPlace()))
                .containsExactly(CandidateSelectionTag.TRANSFER);
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
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(fairness, efficiency, general, filler1, filler2),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                tagSelections
        );

        final List<Place> nextSearchPlaces = candidatePlaceSearchPolicy.selectNextSearchPlaces(
                candidateSelection,
                List.of(fairness.getPlace()),
                ignored -> true,
                4
        );

        assertThat(nextSearchPlaces)
                .extracting(Place::getName)
                .containsExactly("평균후보역", "일반후보역");
    }

    @Test
    @DisplayName("장소명이 같아도 좌표가 다르면 미검색 장소를 다음 검색 대상으로 선택한다")
    void selectNextSearchPlaces_DoesNotSkipUnsearchedPlaceOnlyByPlaceName() {
        final RouteCandidate searched = routeCandidate("동명장소", new Point(127.1, 37.1));
        final RouteCandidate unsearched = routeCandidate("동명장소", new Point(127.2, 37.2));
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(searched));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(unsearched));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(searched, unsearched),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                2,
                false,
                tagSelections
        );

        final List<Place> nextSearchPlaces = candidatePlaceSearchPolicy.selectNextSearchPlaces(
                candidateSelection,
                List.of(searched.getPlace()),
                ignored -> true,
                2
        );

        assertThat(nextSearchPlaces)
                .containsExactly(unsearched.getPlace());
    }

    @Test
    @DisplayName("비어있는 태그의 미검색 후보가 더 이상 없고 일반 태그가 이미 선택되면 추가 조회하지 않는다")
    void selectNextSearchPlaces_DoesNotFillWithGeneralCandidatesWhenGeneralTagAlreadySelected() {
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
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(fairness, efficiency, general, filler1, filler2),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                5,
                false,
                tagSelections
        );

        final List<Place> nextSearchPlaces = candidatePlaceSearchPolicy.selectNextSearchPlaces(
                candidateSelection,
                List.of(fairness.getPlace(), efficiency.getPlace(), general.getPlace()),
                ignored -> true,
                4
        );

        assertThat(nextSearchPlaces)
                .isEmpty();
    }

    @Test
    @DisplayName("일반 태그가 보충 후보로 선택된 상태라면 추가 일반 후보를 조회하지 않는다")
    void selectNextSearchPlaces_DoesNotFillWithGeneralCandidatesWhenFallbackGeneralSelected() {
        final RouteCandidate fairness = routeCandidate("공평후보역");
        final RouteCandidate maxBurden = routeCandidate("최장후보역");
        final RouteCandidate efficiency = routeCandidate("평균후보역");
        final RouteCandidate transfer = routeCandidate("환승후보역");
        final RouteCandidate failedGeneral = routeCandidate("실패일반후보역");
        final RouteCandidate fallbackGeneral = routeCandidate("보충일반후보역");
        final RouteCandidate nextFiller = routeCandidate("다음보충후보역");
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();
        tagSelections.put(CandidateSelectionTag.FAIRNESS, List.of(fairness));
        tagSelections.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, List.of(maxBurden));
        tagSelections.put(CandidateSelectionTag.EFFICIENCY, List.of(efficiency));
        tagSelections.put(CandidateSelectionTag.TRANSFER, List.of(transfer));
        tagSelections.put(CandidateSelectionTag.GENERAL, List.of(failedGeneral));
        final CandidateSelection candidateSelection = new CandidateSelection(
                List.of(fairness, maxBurden, efficiency, transfer, failedGeneral, fallbackGeneral, nextFiller),
                DispersionPolicy.TIER_4,
                DispersionPolicy.TIER_4,
                7,
                false,
                tagSelections
        );
        final List<Place> searchedPlaces = List.of(
                fairness.getPlace(),
                maxBurden.getPlace(),
                efficiency.getPlace(),
                transfer.getPlace(),
                failedGeneral.getPlace(),
                fallbackGeneral.getPlace()
        );

        final RecommendedCandidates recommendedCandidates = candidatePlaceSearchPolicy.select(
                candidateSelection,
                searchedPlaces,
                place -> !place.equals(failedGeneral.getPlace()),
                5
        );
        final List<Place> nextSearchPlaces = candidatePlaceSearchPolicy.selectNextSearchPlaces(
                candidateSelection,
                searchedPlaces,
                place -> !place.equals(failedGeneral.getPlace()),
                4
        );

        assertThat(recommendedCandidates.getTags(fallbackGeneral.getPlace()))
                .containsExactly(CandidateSelectionTag.GENERAL);
        assertThat(nextSearchPlaces)
                .isEmpty();
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
        return routeCandidate(name, new Point(127.0, 37.0));
    }

    private RouteCandidate routeCandidate(final String name, final Point point) {
        final Place start = place("출발역");
        final Place end = new Place(name, point);
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
        return routeCandidateWithTransfers(name, new Point(127.0, 37.0), transferCounts);
    }

    private RouteCandidate routeCandidateWithTransfers(
            final String name,
            final Point point,
            final List<Integer> transferCounts
    ) {
        final Place end = new Place(name, point);
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
