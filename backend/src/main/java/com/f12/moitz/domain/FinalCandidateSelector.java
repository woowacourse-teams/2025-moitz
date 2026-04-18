package com.f12.moitz.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FinalCandidateSelector {

    public FinalCandidateSelectionResult select(
            final CandidateSelectionResult candidateSelectionResult,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final Map<CandidateSelectionTag, Place> taggedPlaces = selectTaggedPlaces(
                candidateSelectionResult,
                searchedPlaces,
                placeCondition
        );
        final List<Place> selectedPlaces = new ArrayList<>(taggedPlaces.values());
        final Set<String> selectedPlaceNames = toPlaceNames(selectedPlaces);
        final Map<Place, CandidateSelectionTag> tagsByPlace = createTagsByPlace(taggedPlaces);

        for (Place place : searchedPlaces) {
            if (selectedPlaces.size() >= limit) {
                break;
            }
            if (selectedPlaceNames.contains(place.getName()) || !placeCondition.test(place)) {
                continue;
            }
            selectedPlaces.add(place);
            selectedPlaceNames.add(place.getName());
            tagsByPlace.put(place, CandidateSelectionTag.GENERAL);
        }

        final List<Place> limitedSelectedPlaces = selectedPlaces.stream()
                .limit(limit)
                .toList();
        return new FinalCandidateSelectionResult(
                limitedSelectedPlaces,
                filterTagsBySelectedPlaces(tagsByPlace, limitedSelectedPlaces)
        );
    }

    public List<Place> selectNextSearchPlaces(
            final CandidateSelectionResult candidateSelectionResult,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final Map<CandidateSelectionTag, Place> selectedByTag = selectTaggedPlaces(
                candidateSelectionResult,
                searchedPlaces,
                placeCondition
        );
        final Set<String> searchedPlaceNames = toPlaceNames(searchedPlaces);
        final Set<String> selectedPlaceNames = toPlaceNames(new ArrayList<>(selectedByTag.values()));
        final Map<String, Place> nextSearchPlaces = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.values()) {
            if (selectedByTag.containsKey(tag)) {
                continue;
            }
            candidateSelectionResult.getTagSelections()
                    .getOrDefault(tag, List.of())
                    .stream()
                    .map(RouteCandidate::getPlace)
                    .filter(place -> !searchedPlaceNames.contains(place.getName()))
                    .filter(place -> !selectedPlaceNames.contains(place.getName()))
                    .findFirst()
                    .ifPresent(place -> nextSearchPlaces.putIfAbsent(place.getName(), place));
        }

        if (!nextSearchPlaces.isEmpty()) {
            return nextSearchPlaces.values().stream()
                    .limit(limit)
                    .toList();
        }

        for (RouteCandidate candidate : candidateSelectionResult.getSelectedCandidates()) {
            if (nextSearchPlaces.size() >= limit) {
                break;
            }
            final Place place = candidate.getPlace();
            if (searchedPlaceNames.contains(place.getName()) || selectedPlaceNames.contains(place.getName())) {
                continue;
            }
            nextSearchPlaces.putIfAbsent(place.getName(), place);
        }

        return nextSearchPlaces.values().stream()
                .limit(limit)
                .toList();
    }

    private Map<CandidateSelectionTag, Place> selectTaggedPlaces(
            final CandidateSelectionResult candidateSelectionResult,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final Set<String> searchedPlaceNames = toPlaceNames(searchedPlaces);
        final Set<String> selectedPlaceNames = new HashSet<>();
        final Map<CandidateSelectionTag, Place> selectedByTag = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.values()) {
            final List<RouteCandidate> tagCandidates = candidateSelectionResult.getTagSelections()
                    .getOrDefault(tag, List.of());
            for (RouteCandidate candidate : tagCandidates) {
                final Place place = candidate.getPlace();
                if (!searchedPlaceNames.contains(place.getName())
                        || selectedPlaceNames.contains(place.getName())
                        || !placeCondition.test(place)) {
                    continue;
                }
                selectedByTag.put(tag, place);
                selectedPlaceNames.add(place.getName());
                break;
            }
        }

        return selectedByTag;
    }

    private Map<Place, CandidateSelectionTag> createTagsByPlace(final Map<CandidateSelectionTag, Place> taggedPlaces) {
        final Map<Place, CandidateSelectionTag> tagsByPlace = new LinkedHashMap<>();
        taggedPlaces.forEach((tag, place) -> tagsByPlace.put(place, tag));
        return tagsByPlace;
    }

    private Map<Place, CandidateSelectionTag> filterTagsBySelectedPlaces(
            final Map<Place, CandidateSelectionTag> tagsByPlace,
            final List<Place> selectedPlaces
    ) {
        final Map<Place, CandidateSelectionTag> filteredTagsByPlace = new LinkedHashMap<>();
        selectedPlaces.forEach(place -> filteredTagsByPlace.put(place, tagsByPlace.get(place)));
        return filteredTagsByPlace;
    }

    private Set<String> toPlaceNames(final List<Place> places) {
        final Set<String> placeNames = new HashSet<>();
        places.forEach(place -> placeNames.add(place.getName()));
        return placeNames;
    }

}
