package com.f12.moitz.application;

import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.SelectedCandidates;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RecommendedCandidateReasonService {

    private final LocationReasonGenerator locationReasonGenerator;

    public RecommendedCandidateReasonService(final LocationReasonGenerator locationReasonGenerator) {
        this.locationReasonGenerator = locationReasonGenerator;
    }

    public Map<Place, ReasonAndDescription> generate(final SelectedCandidates selectedCandidates) {
        final List<Place> places = selectedCandidates.getSelectedPlaces();
        final Map<String, ReasonAndDescription> reasonsByPlaceName = locationReasonGenerator.generateReasons(
                getPlaceNames(places),
                toTagsByPlaceName(selectedCandidates)
        );
        return mapReasonsByPlace(places, reasonsByPlaceName);
    }

    private Map<Place, ReasonAndDescription> mapReasonsByPlace(
            final List<Place> places,
            final Map<String, ReasonAndDescription> reasonsByPlaceName
    ) {
        if (reasonsByPlaceName == null) {
            throw new IllegalStateException("추천 이유 생성 결과가 null입니다.");
        }
        return places.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        place -> getReason(place, reasonsByPlaceName),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private ReasonAndDescription getReason(
            final Place place,
            final Map<String, ReasonAndDescription> reasonsByPlaceName
    ) {
        final ReasonAndDescription reason = reasonsByPlaceName.get(place.getName());
        if (reason == null) {
            throw new IllegalStateException("추천 이유 생성 결과가 누락되었습니다. placeName=" + place.getName());
        }
        return reason;
    }

    private List<String> getPlaceNames(final List<? extends Place> places) {
        return places.stream()
                .map(Place::getName)
                .toList();
    }

    private Map<String, List<CandidateSelectionTag>> toTagsByPlaceName(
            final SelectedCandidates selectedCandidates
    ) {
        return selectedCandidates.getTagsByPlace().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

}
