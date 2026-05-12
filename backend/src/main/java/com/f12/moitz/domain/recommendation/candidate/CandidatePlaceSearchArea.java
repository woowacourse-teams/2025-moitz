package com.f12.moitz.domain.recommendation.candidate;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import java.util.List;
import java.util.Objects;

public class CandidatePlaceSearchArea {

    private final Point center;
    private final int radiusKilometers;

    public CandidatePlaceSearchArea(
            final List<? extends Place> originPlaces,
            final int radiusKilometers
    ) {
        validate(originPlaces, radiusKilometers);
        this.center = calculateCenter(originPlaces);
        this.radiusKilometers = radiusKilometers;
    }

    private void validate(
            final List<? extends Place> originPlaces,
            final int radiusKilometers
    ) {
        if (originPlaces == null || originPlaces.isEmpty()) {
            throw new IllegalArgumentException("출발 지하철역 목록은 비어있거나 null일 수 없습니다.");
        }
        if (originPlaces.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("출발 지하철역 목록에 null이 포함될 수 없습니다.");
        }
        if (radiusKilometers <= 0) {
            throw new IllegalArgumentException("후보역 검색 반경은 0보다 커야 합니다.");
        }
    }

    private Point calculateCenter(final List<? extends Place> originPlaces) {
        final double x = originPlaces.stream()
                .map(Place::getPoint)
                .mapToDouble(Point::getX)
                .average()
                .orElseThrow();
        final double y = originPlaces.stream()
                .map(Place::getPoint)
                .mapToDouble(Point::getY)
                .average()
                .orElseThrow();
        return new Point(x, y);
    }

    public Point getCenter() {
        return center;
    }

    public int getRadiusKilometers() {
        return radiusKilometers;
    }

}
