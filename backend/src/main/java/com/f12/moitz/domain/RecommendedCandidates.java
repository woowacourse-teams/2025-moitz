package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecommendedCandidates {

    private final List<Place> recommendedCandidatePlaces;
    private final Map<Place, List<CandidateSelectionTag>> tagsByPlace;

    public RecommendedCandidates(
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        validate(recommendedCandidatePlaces, tagsByPlace);
        this.recommendedCandidatePlaces = List.copyOf(recommendedCandidatePlaces);
        this.tagsByPlace = normalizeTagsByPlace(recommendedCandidatePlaces, tagsByPlace);
    }

    private void validate(
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        if (recommendedCandidatePlaces == null) {
            throw new IllegalArgumentException("추천 후보 목록은 null일 수 없습니다.");
        }
        if (recommendedCandidatePlaces.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 후보 목록에 null이 포함될 수 없습니다.");
        }
        if (tagsByPlace == null) {
            throw new IllegalArgumentException("추천 후보 태그는 null일 수 없습니다.");
        }
    }

    public CandidateSelectionTag getTag(final Place place) {
        return getTags(place).get(0);
    }

    public List<CandidateSelectionTag> getTags(final Place place) {
        validateRecommendedCandidatePlace(place);
        return tagsByPlace.get(place);
    }

    public List<Place> getPlaces() {
        return recommendedCandidatePlaces;
    }

    public List<String> getPlaceNames() {
        return recommendedCandidatePlaces.stream()
                .map(Place::getName)
                .toList();
    }

    public Map<String, List<CandidateSelectionTag>> getTagsByPlaceName() {
        final Map<String, List<CandidateSelectionTag>> tagsByPlaceName = new LinkedHashMap<>();
        recommendedCandidatePlaces.forEach(place -> tagsByPlaceName.put(
                place.getName(),
                getTags(place)
        ));
        return Collections.unmodifiableMap(tagsByPlaceName);
    }

    public int size() {
        return recommendedCandidatePlaces.size();
    }

    public boolean isEmpty() {
        return recommendedCandidatePlaces.isEmpty();
    }

    private void validateRecommendedCandidatePlace(final Place place) {
        if (place == null) {
            throw new IllegalArgumentException("추천 후보 장소는 null일 수 없습니다.");
        }
        if (!tagsByPlace.containsKey(place)) {
            throw new IllegalArgumentException("추천 후보 태그가 누락되었습니다. 추천 지역: " + place.getName());
        }
    }

    private Map<Place, List<CandidateSelectionTag>> normalizeTagsByPlace(
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        final Map<Place, List<CandidateSelectionTag>> normalizedTagsByPlace = new LinkedHashMap<>();
        recommendedCandidatePlaces.forEach(place -> normalizedTagsByPlace.put(
                place,
                CandidateSelectionTag.normalize(tagsByPlace.get(place))
        ));
        return Collections.unmodifiableMap(normalizedTagsByPlace);
    }

}
