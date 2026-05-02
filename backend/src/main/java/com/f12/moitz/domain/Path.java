package com.f12.moitz.domain;

import com.f12.moitz.domain.subway.SubwayLine;
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
    private SubwayLine subwayLine;

    public static Path subway(
            final Place start,
            final Place end,
            final Duration travelTime,
            final SubwayLine subwayLine
    ) {
        return new Path(start, end, TravelMethod.SUBWAY, travelTime, subwayLine);
    }

    public static Path subway(
            final Place start,
            final Place end,
            final int travelTime,
            final SubwayLine subwayLine
    ) {
        return subway(start, end, Duration.ofSeconds(travelTime), subwayLine);
    }

    public static Path transfer(final Place station, final Duration travelTime) {
        return new Path(station, station, TravelMethod.TRANSFER, travelTime, null);
    }

    public static Path transfer(final Place station, final int travelTime) {
        return transfer(station, Duration.ofSeconds(travelTime));
    }

    public Path(
            final Place start,
            final Place end,
            final TravelMethod travelMethod,
            final Duration travelTime,
            final SubwayLine subwayLine
    ) {
        validate(start, end, travelMethod, travelTime);
        validateStartToEnd(start, end, travelMethod);
        validateSubwayLine(travelMethod, subwayLine);
        this.start = start;
        this.end = end;
        this.travelMethod = travelMethod;
        this.travelTime = travelTime;
        this.subwayLine = subwayLine;
    }

    public Path(
            final Place start,
            final Place end,
            final TravelMethod travelMethod,
            final int travelTime,
            final SubwayLine subwayLine
    ) {
        this(start, end, travelMethod, Duration.ofSeconds(travelTime), subwayLine);
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
            throw new IllegalArgumentException(
                    String.format(
                            "시작 장소(%s)와 끝 장소(%s)는 같을 수 없습니다.",
                            start.getName(), end.getName()
                    )
            );
        }
        if (travelMethod.isTransfer() && !start.equals(end)) {
            throw new IllegalArgumentException(
                    String.format(
                            "환승 통로 이동에서의 시작 장소(%s)와 끝 장소(%s)는 다를 수 없습니다.",
                            start.getName(), end.getName()
                    )
            );
        }
    }

    private void validateSubwayLine(final TravelMethod travelMethod, final SubwayLine subwayLine) {
        if (travelMethod != TravelMethod.SUBWAY && subwayLine != null) {
            throw new IllegalStateException("지하철이 아니라면 호선 정보를 지닐 수 없습니다.");
        }
        if (travelMethod == TravelMethod.SUBWAY && subwayLine == null) {
            throw new IllegalStateException("지하철인 경우 지하철 호선 정보가 필수입니다.");
        }
    }

}
