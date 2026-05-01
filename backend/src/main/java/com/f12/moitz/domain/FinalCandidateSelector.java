package com.f12.moitz.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FinalCandidateSelector {

    public SelectedCandidates select(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final TaggedPlaceSelection taggedPlaceSelection = selectTaggedPlaces(
                candidateSelection,
                searchedPlaces,
                placeCondition
        );
        final List<Place> selectedPlaces = new ArrayList<>(taggedPlaceSelection.selectedPlaces());
        final Set<String> selectedPlaceNames = toPlaceNames(selectedPlaces);
        final Map<Place, List<CandidateSelectionTag>> tagsByPlace = new LinkedHashMap<>(
                taggedPlaceSelection.tagsByPlace()
        );

        for (Place place : searchedPlaces) {
            if (selectedPlaces.size() >= limit) {
                break;
            }
            if (selectedPlaceNames.contains(place.getName()) || !placeCondition.test(place)) {
                continue;
            }
            selectedPlaces.add(place);
            selectedPlaceNames.add(place.getName());
            tagsByPlace.put(place, List.of(CandidateSelectionTag.GENERAL));
        }

        final List<Place> limitedSelectedPlaces = selectedPlaces.stream()
                .limit(limit)
                .toList();
        return new SelectedCandidates(
                limitedSelectedPlaces,
                filterTagsBySelectedPlaces(tagsByPlace, limitedSelectedPlaces)
        );
    }

    public List<Place> selectNextSearchPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition,
            final int limit
    ) {
        final Map<CandidateSelectionTag, Place> selectedByTag = selectTaggedPlaces(
                candidateSelection,
                searchedPlaces,
                placeCondition
        ).selectedByTag();
        final Set<String> searchedPlaceNames = toPlaceNames(searchedPlaces);
        final Set<String> selectedPlaceNames = toPlaceNames(new ArrayList<>(selectedByTag.values()));
        final Map<String, Place> nextSearchPlaces = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.orderedValues()) {
            if (selectedByTag.containsKey(tag)) {
                continue;
            }
            candidateSelection.getTagSelections()
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

        for (RouteCandidate candidate : candidateSelection.getSelectedCandidates()) {
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

    private TaggedPlaceSelection selectTaggedPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Predicate<Place> placeCondition
    ) {
        final Set<String> searchedPlaceNames = toPlaceNames(searchedPlaces);
        final Set<String> selectedPlaceNames = new HashSet<>();
        final Map<CandidateSelectionTag, Place> selectedByTag = new LinkedHashMap<>();
        final Map<Place, Set<CandidateSelectionTag>> tagsByPlace = new LinkedHashMap<>();

        for (CandidateSelectionTag tag : CandidateSelectionTag.orderedValues()) {
            final List<RouteCandidate> tagCandidates = candidateSelection.getTagSelections()
                    .getOrDefault(tag, List.of());
            for (RouteCandidate candidate : tagCandidates) {
                final Place place = candidate.getPlace();
                if (!searchedPlaceNames.contains(place.getName()) || !placeCondition.test(place)) {
                    continue;
                }
                if (selectedPlaceNames.contains(place.getName())) {
                    addTag(tagsByPlace, place, tag);
                    continue;
                }
                selectedByTag.put(tag, place);
                selectedPlaceNames.add(place.getName());
                addTag(tagsByPlace, place, tag);
                break;
            }
        }

        return new TaggedPlaceSelection(
                new ArrayList<>(selectedByTag.values()),
                selectedByTag,
                copyTagsByPlace(tagsByPlace)
        );
    }

    private void addTag(
            final Map<Place, Set<CandidateSelectionTag>> tagsByPlace,
            final Place place,
            final CandidateSelectionTag tag
    ) {
        final Set<CandidateSelectionTag> tags = tagsByPlace.computeIfAbsent(place, ignored -> new LinkedHashSet<>());
        if (tag != CandidateSelectionTag.GENERAL) {
            tags.add(tag);
            return;
        }

        if (tags.isEmpty()) {
            tags.add(CandidateSelectionTag.GENERAL);
        }
    }

    private Map<Place, List<CandidateSelectionTag>> copyTagsByPlace(
            final Map<Place, Set<CandidateSelectionTag>> tagsByPlace
    ) {
        final Map<Place, List<CandidateSelectionTag>> copiedTagsByPlace = new LinkedHashMap<>();
        tagsByPlace.forEach((place, tags) -> copiedTagsByPlace.put(place, List.copyOf(tags)));
        return copiedTagsByPlace;
    }

    private Map<Place, List<CandidateSelectionTag>> filterTagsBySelectedPlaces(
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final List<Place> selectedPlaces
    ) {
        final Map<Place, List<CandidateSelectionTag>> filteredTagsByPlace = new LinkedHashMap<>();
        selectedPlaces.forEach(place -> filteredTagsByPlace.put(
                place,
                tagsByPlace.getOrDefault(place, List.of(CandidateSelectionTag.GENERAL))
        ));
        return filteredTagsByPlace;
    }

    private Set<String> toPlaceNames(final List<Place> places) {
        final Set<String> placeNames = new HashSet<>();
        places.forEach(place -> placeNames.add(place.getName()));
        return placeNames;
    }

    private record TaggedPlaceSelection(
            List<Place> selectedPlaces,
            Map<CandidateSelectionTag, Place> selectedByTag,
            Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {

    }

}
