package com.f12.moitz.domain.recommendation;

import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import java.util.List;
import java.util.stream.Collectors;

public record RecommendationReason(
        String description,
        String reason
) {

    public RecommendationReason {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("추천 설명은 비어 있을 수 없습니다.");
        }
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("추천 이유는 비어 있을 수 없습니다.");
        }
    }

    public static RecommendationReason fromSelectionTags(
            final String placeName,
            final List<CandidateSelectionTag> tags
    ) {
        validatePlaceName(placeName);
        final List<CandidateSelectionTag> normalizedTags = CandidateSelectionTag.normalize(tags);
        return new RecommendationReason(
                createHashtagDescription(normalizedTags),
                createReason(placeName, normalizedTags)
        );
    }

    private static void validatePlaceName(final String placeName) {
        if (placeName == null || placeName.isBlank()) {
            throw new IllegalArgumentException("추천 장소 이름은 비어 있을 수 없습니다.");
        }
    }

    private static String createHashtagDescription(final List<CandidateSelectionTag> tags) {
        return tags.stream()
                .map(CandidateSelectionTag::getHashtag)
                .distinct()
                .collect(Collectors.joining(" "));
    }

    private static String createReason(final String placeName, final List<CandidateSelectionTag> tags) {
        final String descriptions = tags.stream()
                .map(CandidateSelectionTag::getDescription)
                .distinct()
                .collect(Collectors.joining(", "));
        return "%s은 %s을 반영해 추천된 만남 장소입니다.".formatted(placeName, descriptions);
    }

}
