package com.f12.moitz.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class CandidatePlaceSearchPolicy {

    public RecommendedCandidates select(
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
        final List<Place> recommendedCandidatePlaces = new ArrayList<>(taggedPlaceSelection.recommendedCandidatePlaces());
        final Set<String> recommendedCandidatePlaceNames = toPlaceNames(recommendedCandidatePlaces);
        final Map<Place, List<CandidateSelectionTag>> tagsByPlace = new LinkedHashMap<>(
                taggedPlaceSelection.tagsByPlace()
        );

        for (Place place : searchedPlaces) {
            if (recommendedCandidatePlaces.size() >= limit) {
                break;
            }
            if (recommendedCandidatePlaceNames.contains(place.getName()) || !placeCondition.test(place)) {
                continue;
            }
            recommendedCandidatePlaces.add(place);
            recommendedCandidatePlaceNames.add(place.getName());
            tagsByPlace.put(place, List.of(CandidateSelectionTag.GENERAL));
        }

        final List<Place> limitedRecommendedCandidatePlaces = recommendedCandidatePlaces.stream()
                .limit(limit)
                .toList();
        return new RecommendedCandidates(
                limitedRecommendedCandidatePlaces,
                normalizeTransferTags(
                        candidateSelection,
                        limitedRecommendedCandidatePlaces,
                        tagsByPlace
                )
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

        for (RouteCandidate candidate : candidateSelection.getSearchCandidates()) {
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

    private Set<String> toPlaceNames(final List<Place> places) {
        final Set<String> placeNames = new HashSet<>();
        places.forEach(place -> placeNames.add(place.getName()));
        return placeNames;
    }

    private Map<Place, List<CandidateSelectionTag>> normalizeTransferTags(
            final CandidateSelection candidateSelection,
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        final Map<Place, RouteCandidate> candidatesByPlace = candidatesByPlace(candidateSelection);
        final List<TransferBurden> transferBurdens = recommendedCandidatePlaces.stream()
                .map(candidatesByPlace::get)
                .filter(Objects::nonNull)
                .map(RouteCandidate::calculateTransferBurden)
                .toList();

        if (transferBurdens.isEmpty()) {
            return tagsByPlace;
        }

        final TransferBurden bestTransferBurden = transferBurdens.stream()
                .min(TransferBurden::compareTo)
                .orElseThrow();
        final Set<Place> transferCandidatePlaces = transferCandidatePlaces(candidateSelection);
        final Map<Place, List<CandidateSelectionTag>> normalizedTagsByPlace = new LinkedHashMap<>();
        recommendedCandidatePlaces.forEach(place -> normalizedTagsByPlace.put(
                place,
                normalizeTransferTag(place, tagsByPlace, candidatesByPlace, transferCandidatePlaces, bestTransferBurden)
        ));
        return normalizedTagsByPlace;
    }

    private Set<Place> transferCandidatePlaces(final CandidateSelection candidateSelection) {
        final Set<Place> transferCandidatePlaces = new HashSet<>();
        candidateSelection.getTagSelections()
                .getOrDefault(CandidateSelectionTag.TRANSFER, List.of())
                .forEach(candidate -> transferCandidatePlaces.add(candidate.getPlace()));
        return transferCandidatePlaces;
    }

    private Map<Place, RouteCandidate> candidatesByPlace(final CandidateSelection candidateSelection) {
        final Map<Place, RouteCandidate> candidatesByPlace = new LinkedHashMap<>();
        candidateSelection.getSearchCandidates()
                .forEach(candidate -> candidatesByPlace.putIfAbsent(candidate.getPlace(), candidate));
        candidateSelection.getTagSelections()
                .values()
                .forEach(candidates -> candidates.forEach(candidate ->
                        candidatesByPlace.putIfAbsent(candidate.getPlace(), candidate)));
        return candidatesByPlace;
    }

    private List<CandidateSelectionTag> normalizeTransferTag(
            final Place place,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final Map<Place, RouteCandidate> candidatesByPlace,
            final Set<Place> transferCandidatePlaces,
            final TransferBurden bestTransferBurden
    ) {
        final List<CandidateSelectionTag> tags = tagsByPlace.getOrDefault(place, List.of(CandidateSelectionTag.GENERAL));
        final RouteCandidate candidate = candidatesByPlace.get(place);
        final boolean isBestTransfer = candidate != null
                && candidate.calculateTransferBurden().compareTo(bestTransferBurden) == 0;

        if (isBestTransfer && transferCandidatePlaces.contains(place)) {
            if (tags.contains(CandidateSelectionTag.TRANSFER)) {
                return tags;
            }
            final List<CandidateSelectionTag> tagsWithTransfer = new ArrayList<>(tags);
            tagsWithTransfer.add(CandidateSelectionTag.TRANSFER);
            return tagsWithTransfer;
        }
        return tags.stream()
                .filter(tag -> tag != CandidateSelectionTag.TRANSFER)
                .toList();
    }

    private record TaggedPlaceSelection(
            List<Place> recommendedCandidatePlaces,
            Map<CandidateSelectionTag, Place> selectedByTag,
            Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {

    }

}
