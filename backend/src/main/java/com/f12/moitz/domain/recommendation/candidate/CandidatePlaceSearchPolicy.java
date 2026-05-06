package com.f12.moitz.domain.recommendation.candidate;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class CandidatePlaceSearchPolicy {

    public RecommendedCandidates select(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final TaggedPlaceSelection taggedPlaceSelection = selectCurrentPlaces(
                candidateSelection,
                searchedPlaces,
                placeCondition
        );

        final List<Place> limitedRecommendedCandidatePlaces = taggedPlaceSelection.recommendedCandidatePlaces().stream()
                .limit(limit)
                .toList();
        return new RecommendedCandidates(
                limitedRecommendedCandidatePlaces,
                taggedPlaceSelection.tagsByPlace()
        );
    }

    public List<Place> selectNextSearchPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final Map<CandidateSelectionTag, Place> selectedByTag = selectCurrentPlaces(
                candidateSelection,
                searchedPlaces,
                placeCondition
        ).selectedByTag();
        final Set<Place> searchedPlaceSet = toPlaceSet(searchedPlaces);
        final Set<Place> selectedPlaceSet = toPlaceSet(new ArrayList<>(selectedByTag.values()));
        final Map<Place, Place> nextSearchPlaces = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.orderedValues()) {
            if (selectedByTag.containsKey(tag)) {
                continue;
            }
            candidateSelection.getCandidatesByTag(tag)
                    .stream()
                    .map(RouteCandidate::getPlace)
                    .filter(place -> !searchedPlaceSet.contains(place))
                    .filter(place -> !selectedPlaceSet.contains(place))
                    .findFirst()
                    .ifPresent(place -> nextSearchPlaces.putIfAbsent(place, place));
        }

        if (!nextSearchPlaces.isEmpty() || selectedByTag.containsKey(CandidateSelectionTag.GENERAL)) {
            return nextSearchPlaces.values().stream()
                    .limit(limit)
                    .toList();
        }

        for (RouteCandidate candidate : candidateSelection.getSearchCandidates()) {
            if (nextSearchPlaces.size() >= limit) {
                break;
            }
            final Place place = candidate.getPlace();
            if (searchedPlaceSet.contains(place) || selectedPlaceSet.contains(place)) {
                continue;
            }
            nextSearchPlaces.putIfAbsent(place, place);
        }

        return nextSearchPlaces.values().stream()
                .limit(limit)
                .toList();
    }

    private TaggedPlaceSelection selectCurrentPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final TaggedPlaceSelection taggedPlaceSelection = selectTaggedPlaces(
                candidateSelection,
                searchedPlaces,
                placeCondition
        );
        final List<Place> recommendedCandidatePlaces = new ArrayList<>(taggedPlaceSelection.recommendedCandidatePlaces());
        final Map<CandidateSelectionTag, Place> selectedByTag = new LinkedHashMap<>(
                taggedPlaceSelection.selectedByTag()
        );
        final Map<Place, List<CandidateSelectionTag>> tagsByPlace = new LinkedHashMap<>(
                taggedPlaceSelection.tagsByPlace()
        );
        final Set<Place> recommendedCandidatePlaceSet = toPlaceSet(recommendedCandidatePlaces);

        if (selectedByTag.containsKey(CandidateSelectionTag.GENERAL)) {
            return new TaggedPlaceSelection(recommendedCandidatePlaces, selectedByTag, tagsByPlace);
        }

        for (Place place : searchedPlaces) {
            if (recommendedCandidatePlaceSet.contains(place) || !placeCondition.test(place)) {
                continue;
            }
            recommendedCandidatePlaces.add(place);
            recommendedCandidatePlaceSet.add(place);
            selectedByTag.put(CandidateSelectionTag.GENERAL, place);
            tagsByPlace.put(place, List.of(CandidateSelectionTag.GENERAL));
            break;
        }

        return new TaggedPlaceSelection(recommendedCandidatePlaces, selectedByTag, tagsByPlace);
    }

    private TaggedPlaceSelection selectTaggedPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final Set<Place> searchedPlaceSet = toPlaceSet(searchedPlaces);
        final Set<Place> selectedPlaceSet = new HashSet<>();
        final Map<CandidateSelectionTag, Place> selectedByTag = new LinkedHashMap<>();
        final Map<Place, List<CandidateSelectionTag>> tagsByPlace = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.orderedValues()) {
            final List<RouteCandidate> tagCandidates = candidateSelection.getCandidatesByTag(tag);
            for (RouteCandidate candidate : tagCandidates) {
                final Place place = candidate.getPlace();
                if (!searchedPlaceSet.contains(place) || !placeCondition.test(place)) {
                    continue;
                }
                if (selectedPlaceSet.contains(place)) {
                    continue;
                }
                selectedByTag.put(tag, place);
                selectedPlaceSet.add(place);
                tagsByPlace.put(place, List.of(tag));
                break;
            }
        }

        return new TaggedPlaceSelection(
                new ArrayList<>(selectedByTag.values()),
                selectedByTag,
                tagsByPlace
        );
    }

    private Set<Place> toPlaceSet(final List<Place> places) {
        return new HashSet<>(places);
    }

    private record TaggedPlaceSelection(
            List<Place> recommendedCandidatePlaces,
            Map<CandidateSelectionTag, Place> selectedByTag,
            Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {

    }

}
