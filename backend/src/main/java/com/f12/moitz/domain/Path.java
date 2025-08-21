package com.f12.moitz.domain;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Path {

    private Place start;
    private Place end;
    private TravelMethod travelMethod;
    private Duration travelTime;
    private String subwayLineName;

    public Path(
            final Place start,
            final Place end,
            final TravelMethod travelMethod,
            final Duration travelTime,
            final String subwayLineName
    ) {
        validate(start, end, travelMethod, travelTime);
        validateStartToEnd(start, end, travelMethod);
        validateSubwayLineName(travelMethod, subwayLineName);
        this.start = start;
        this.end = end;
        this.travelMethod = travelMethod;
        this.travelTime = travelTime;
        this.subwayLineName = subwayLineName;
    }

    public Path(
            final Place start,
            final Place end,
            final TravelMethod travelMethod,
            final int travelTime,
            final String subwayLineName
    ) {
        this(start, end, travelMethod, Duration.ofSeconds(travelTime), subwayLineName);
    }

    private void validate(final Place start, final Place end, final TravelMethod travelMethod, final Duration travelTime) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("시작 장소와 끝 장소는 필수입니다.");
        }
        if (travelMethod == null) {
            throw new IllegalArgumentException("이동 수단은 필수입니다.");
        }
        if (travelTime == null || travelTime.isNegative()) {
            throw new IllegalArgumentException("이동 시간은 null이거나 음수일 수 없습니다.");
        }
    }

    private void validateStartToEnd(final Place start, final Place end,  final TravelMethod travelMethod) {
        if (!travelMethod.isTransfer() && start.equals(end)) {
            throw new IllegalArgumentException("시작 장소와 끝 장소는 같을 수 없습니다.");
        }
        if (travelMethod.isTransfer() && !start.equals(end)) {
            throw new IllegalArgumentException("환승 통로 이동에서의 시작과 끝 장소는 다를 수 없습니다..");
        }
    }

    private void validateSubwayLineName(final TravelMethod travelMethod, final String subwayLineName) {
        if (travelMethod != TravelMethod.SUBWAY && subwayLineName != null) {
            throw new IllegalStateException("지하철이 아니라면 호선 정보를 지닐 수 없습니다.");
        }
        if (travelMethod == TravelMethod.SUBWAY && subwayLineName == null) {
            throw new IllegalStateException("지하철인 경우 지하철 노선명이 필수입니다.");
        }
    }

}
