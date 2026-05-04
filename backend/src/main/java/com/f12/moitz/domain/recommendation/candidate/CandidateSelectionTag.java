package com.f12.moitz.domain.recommendation.candidate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public enum CandidateSelectionTag {

    FAIRNESS(1, "#공평", "모든 참여자의 이동 시간이 최대한 비슷한 기준"),
    MAX_BURDEN_RELIEF(2, "#최대부담완화", "가장 오래 이동하는 사람의 부담을 줄이는 기준"),
    EFFICIENCY(3, "#평균최소", "전체 참여자의 평균 이동 시간이 짧은 기준"),
    TRANSFER(4, "#최소환승", "환승 부담이 적은 기준"),
    GENERAL(5, "#종합추천", "이동 시간, 환승, 균형을 종합한 기준");

    private final int priority;
    private final String hashtag;
    private final String description;

    CandidateSelectionTag(final int priority, final String hashtag, final String description) {
        this.priority = priority;
        this.hashtag = hashtag;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getHashtag() {
        return hashtag;
    }

    public String getDescription() {
        return description;
    }

    public static List<CandidateSelectionTag> orderedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(CandidateSelectionTag::getPriority))
                .toList();
    }

    public static List<CandidateSelectionTag> normalize(final List<CandidateSelectionTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of(GENERAL);
        }

        final List<CandidateSelectionTag> resolvedTags = tags.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (resolvedTags.isEmpty()) {
            return List.of(GENERAL);
        }
        if (resolvedTags.size() == 1) {
            return resolvedTags;
        }
        return resolvedTags.stream()
                .filter(tag -> tag != GENERAL)
                .toList();
    }

}
