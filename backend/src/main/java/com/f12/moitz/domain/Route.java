package com.f12.moitz.domain;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class Route {

    private final Place startPlace;
    private final Place endPlace;
    private final List<Path> paths;

    public Route(final Place startPlace, final Place endPlace, final List<Path> paths) {
        validate(startPlace, endPlace, paths);
        validateCollectPaths(startPlace, endPlace, paths);
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.paths = paths;
    }

    private void validate(final Place startPlace, final Place endPlace, final List<Path> paths) {
        if (startPlace == null || endPlace == null) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION);
        }
        if (paths == null || paths.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION);
        }
    }

    private void validateCollectPaths(final Place startPlace, final Place endPlace, final List<Path> paths) {
        if (!paths.getFirst().getStart().equals(startPlace) || !paths.getLast().getEnd().equals(endPlace)) {
            throw new IllegalStateException("경로의 시작점과 끝점이 일치하지 않습니다.");
        }
    }

    public int calculateTotalTravelTime() {
        return paths.stream()
                .mapToInt(path -> (int) path.getTravelTime().toMinutes())
                .sum();
    }

}


/*

@Getter
public class Route {

    private final List<Path> paths;

    public Route(final List<Path> paths) {
        validate(paths);
        this.paths = paths;
    }

    private void validate(final List<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION);
        }
    }

}

 */
