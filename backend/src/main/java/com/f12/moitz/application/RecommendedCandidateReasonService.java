package com.f12.moitz.application;

import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RecommendationReason;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RecommendedCandidateReasonService {

    private final LocationReasonGenerator locationReasonGenerator;

    public RecommendedCandidateReasonService(final LocationReasonGenerator locationReasonGenerator) {
        this.locationReasonGenerator = locationReasonGenerator;
    }

    public Map<Place, RecommendationReason> generate(final RecommendedCandidates recommendedCandidates) {
        final List<Place> places = recommendedCandidates.getPlaces();
        final Map<String, ReasonAndDescription> reasonsByPlaceName = locationReasonGenerator.generateReasons(
                recommendedCandidates.getPlaceNames(),
                recommendedCandidates.getTagsByPlaceName()
        );
        return mapReasonsByPlace(places, reasonsByPlaceName);
    }

    private Map<Place, RecommendationReason> mapReasonsByPlace(
            final List<Place> places,
            final Map<String, ReasonAndDescription> reasonsByPlaceName
    ) {
        if (reasonsByPlaceName == null) {
            throw new IllegalStateException("추천 이유 생성 결과가 null입니다.");
        }
        return places.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        place -> toRecommendationReason(getReason(place, reasonsByPlaceName)),
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

    private RecommendationReason toRecommendationReason(final ReasonAndDescription reason) {
        return new RecommendationReason(reason.description(), reason.reason());
    }

}
