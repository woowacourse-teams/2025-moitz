package com.f12.moitz.domain;

import java.time.Duration;
import lombok.Getter;

@Getter
public class Path {

    private final Place start;
    private final Place end;
    private final TravelMethod travelMethod;
    private final Duration travelTime;

    public Path(final Place start, final Place end, final TravelMethod travelMethod, final int travelTime) {
        validate(start, end, travelMethod, travelTime);
        validateStartToEnd(start, end, travelMethod);
        this.start = start;
        this.end = end;
        this.travelMethod = travelMethod;
        this.travelTime = Duration.ofMinutes(travelTime);
    }

    private void validate(final Place start, final Place end, final TravelMethod travelMethod, final int travelTime) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("시작 장소와 끝 장소는 필수입니다.");
        }
        if (travelMethod == null) {
            throw new IllegalArgumentException("이동 수단은 필수입니다.");
        }
        if (travelTime < 0) {
            throw new IllegalArgumentException("이동 시간은 음수일 수 없습니다.");
        }
    }

    private void validateStartToEnd(final Place start, final Place end,  final TravelMethod travelMethod) {
        if (travelMethod != TravelMethod.TRANSFER && start.equals(end)) {
            throw new IllegalArgumentException("시작 장소와 끝 장소는 같을 수 없습니다.");
        }
        if (travelMethod == TravelMethod.TRANSFER && !start.equals(end)) {
            throw new IllegalArgumentException("환승 통로 이동에서의 시작과 끝 장소는 다를 수 없습니다..");
        }
    }

}
