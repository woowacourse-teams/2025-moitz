package com.f12.moitz.domain;

public class FairnessTolerancePolicy {

    private static final int SHORT_AVERAGE_TRAVEL_TIME_MINUTES = 10;
    private static final int MEDIUM_AVERAGE_TRAVEL_TIME_MINUTES = 30;
    private static final double SHORT_TRAVEL_TIME_TOLERANCE_RATIO = 0.5;
    private static final double MEDIUM_TRAVEL_TIME_TOLERANCE_RATIO = 0.3;
    private static final double LONG_TRAVEL_TIME_TOLERANCE_RATIO = 0.2;

    public boolean isTolerable(final FairnessScore score) {
        validate(score);
        return score.getTimeDiff() <= resolveToleranceMinutes(score);
    }

    public int resolveToleranceMinutes(final FairnessScore score) {
        validate(score);
        return (int) Math.ceil(score.getAverageTravelTime() * resolveToleranceRatio(score));
    }

    private void validate(final FairnessScore score) {
        if (score == null) {
            throw new IllegalArgumentException("공평 점수는 필수입니다.");
        }
    }

    private double resolveToleranceRatio(final FairnessScore score) {
        if (score.getAverageTravelTime() <= SHORT_AVERAGE_TRAVEL_TIME_MINUTES) {
            return SHORT_TRAVEL_TIME_TOLERANCE_RATIO;
        }
        if (score.getAverageTravelTime() <= MEDIUM_AVERAGE_TRAVEL_TIME_MINUTES) {
            return MEDIUM_TRAVEL_TIME_TOLERANCE_RATIO;
        }
        return LONG_TRAVEL_TIME_TOLERANCE_RATIO;
    }

}
