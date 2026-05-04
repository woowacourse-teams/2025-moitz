package com.f12.moitz.application.recommendation;

import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RouteOriginPreparationService {

    private final SubwayStationService subwayStationService;

    public RouteOriginPreparationService(final SubwayStationService subwayStationService) {
        this.subwayStationService = subwayStationService;
    }

    public RouteOriginPreparationResult prepare(final List<String> startingPlaceNames) {
        final List<SubwayStation> originStations = getByNames(startingPlaceNames);
        return new RouteOriginPreparationResult(
                originStations,
                createRouteOrigins(originStations)
        );
    }

    private List<SubwayStation> getByNames(final List<String> names) {
        return names.stream()
                .map(name -> subwayStationService.findByName(name)
                        .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION)))
                .toList();
    }

    private RouteOrigins createRouteOrigins(final List<SubwayStation> originStations) {
        try {
            return new RouteOrigins(originStations);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, getPlaceNames(originStations));
        }
    }

    private List<String> getPlaceNames(final List<? extends Place> places) {
        return places.stream()
                .map(Place::getName)
                .toList();
    }

}
