package com.f12.moitz.domain;

public enum CandidateSelectionTag {

    FAIRNESS("이동 시간이 고르게 분포한 추천"),
    MAX_BURDEN_RELIEF("가장 오래 이동하는 사람의 부담을 줄인 추천"),
    EFFICIENCY("전체 평균 이동 시간이 짧은 추천"),
    TRANSFER("환승 부담이 적은 추천"),
    GENERAL("이동 시간, 환승, 균형을 종합한 추천");

    private final String description;

    CandidateSelectionTag(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
