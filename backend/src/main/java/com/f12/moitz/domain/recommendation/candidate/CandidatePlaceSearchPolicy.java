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

        if (!nextSearchPlaces.isEmpty() || hasGeneralSelection(selectedByTag)) {
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
        appendGeneralFallback(taggedPlaceSelection, searchedPlaces, placeCondition);
        return taggedPlaceSelection;
    }

    private void appendGeneralFallback(
            final TaggedPlaceSelection taggedPlaceSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final Map<CandidateSelectionTag, Place> selectedByTag = taggedPlaceSelection.selectedByTag();
        if (hasGeneralSelection(selectedByTag)) {
            return;
        }

        final List<Place> recommendedCandidatePlaces = taggedPlaceSelection.recommendedCandidatePlaces();
        final Set<Place> recommendedCandidatePlaceSet = toPlaceSet(recommendedCandidatePlaces);
        final Map<Place, CandidateSelectionTag> tagsByPlace = taggedPlaceSelection.tagsByPlace();

        for (Place place : searchedPlaces) {
            if (recommendedCandidatePlaceSet.contains(place) || !placeCondition.test(place)) {
                continue;
            }
            recommendedCandidatePlaces.add(place);
            selectedByTag.put(CandidateSelectionTag.GENERAL, place);
            tagsByPlace.put(place, CandidateSelectionTag.GENERAL);
            return;
        }
    }

    private boolean hasGeneralSelection(final Map<CandidateSelectionTag, Place> selectedByTag) {
        return selectedByTag.containsKey(CandidateSelectionTag.GENERAL);
    }

    private TaggedPlaceSelection selectTaggedPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final Set<Place> searchedPlaceSet = toPlaceSet(searchedPlaces);
        final Set<Place> selectedPlaceSet = new HashSet<>();
        final Map<CandidateSelectionTag, Place> selectedByTag = new LinkedHashMap<>();
        final Map<Place, CandidateSelectionTag> tagsByPlace = new LinkedHashMap<>();

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
                tagsByPlace.put(place, tag);
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
            Map<Place, CandidateSelectionTag> tagsByPlace
    ) {

    }

}
