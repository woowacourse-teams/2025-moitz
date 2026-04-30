package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.CandidateSelectionTag;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class LocationReasonGeneratorAdapter implements LocationReasonGenerator {

    @Override
    public Map<String, ReasonAndDescription> generateReasons(
            final List<String> selectedPlaces,
            final Map<String, List<CandidateSelectionTag>> tagsByPlaceName
    ) {
        final Map<String, ReasonAndDescription> reasons = new LinkedHashMap<>();
        selectedPlaces.forEach(placeName -> {
            final List<CandidateSelectionTag> tags = resolveTags(placeName, tagsByPlaceName);
            reasons.put(placeName, new ReasonAndDescription(
                    createHashtagReason(tags),
                    createReason(placeName, tags)
            ));
        });
        return reasons;
    }

    private List<CandidateSelectionTag> resolveTags(
            final String placeName,
            final Map<String, List<CandidateSelectionTag>> tagsByPlaceName
    ) {
        final Map<String, List<CandidateSelectionTag>> resolvedTagsByPlaceName =
                tagsByPlaceName == null ? Map.of() : tagsByPlaceName;
        final List<CandidateSelectionTag> tags = resolvedTagsByPlaceName.getOrDefault(placeName, List.of());
        return CandidateSelectionTag.normalize(tags);
    }

    private String createHashtagReason(final List<CandidateSelectionTag> tags) {
        return tags.stream()
                .map(CandidateSelectionTag::getHashtag)
                .distinct()
                .collect(Collectors.joining(" "));
    }

    private String createReason(final String placeName, final List<CandidateSelectionTag> tags) {
        final String descriptions = tags.stream()
                .map(CandidateSelectionTag::getDescription)
                .distinct()
                .collect(Collectors.joining(", "));
        return "%s은 %s을 반영해 추천된 만남 장소입니다.".formatted(placeName, descriptions);
    }
}
