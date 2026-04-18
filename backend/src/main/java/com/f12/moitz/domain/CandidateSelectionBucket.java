package com.f12.moitz.domain;

public enum CandidateSelectionBucket {

    EFFICIENCY("이동 효율 후보"),
    FAIRNESS("공평성 후보"),
    MAX_BURDEN_RELIEF("최장 부담 완화 후보"),
    TRANSFER("환승 부담 후보");

    private final String description;

    CandidateSelectionBucket(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
