package com.f12.moitz.domain;

public enum CandidateSelectionTag {

    FAIRNESS("이동시간 편차 최소"),
    MAX_BURDEN_RELIEF("최장 이동시간 최소"),
    EFFICIENCY("평균 이동시간 최소"),
    TRANSFER("환승 최소"),
    GENERAL("일반 추천");

    private final String description;

    CandidateSelectionTag(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
