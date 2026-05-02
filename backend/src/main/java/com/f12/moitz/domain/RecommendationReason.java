package com.f12.moitz.domain;

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

}
