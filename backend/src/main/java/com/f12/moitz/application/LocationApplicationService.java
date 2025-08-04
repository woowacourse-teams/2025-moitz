package com.f12.moitz.application;

import com.f12.moitz.application.dto.LocationRecommendRequest;
import com.f12.moitz.application.dto.LocationRecommendResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.utils.LocationMapper;
import com.f12.moitz.domain.Location;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.service.LocationDomainService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationApplicationService {

    private final LocationDomainService locationDomainService;

    private final LocationRecommender locationRecommender;
    private final PlaceRecommender placeRecommender;

    private final LocationMapper locationMapper;

    public List<LocationRecommendResponse> recommendLocation(final LocationRecommendRequest request) {
        final String requirement = request.requirement();

        final List<Place> startingPlaces = placeRecommender.findPlacesByNames(request.startingPlaceNames());
        final Map<Place, String> generatedPlaces = locationRecommender.recommendLocations(request.startingPlaceNames(), requirement);
        final Map<Place, List<RecommendedPlace>> recommendedPlaces = placeRecommender.recommendPlaces(
                new HashSet<>(generatedPlaces.keySet()), requirement);

        List<Location> sortedLocations = locationDomainService.findAndSort(startingPlaces, generatedPlaces, recommendedPlaces);

        return locationMapper.toResponse(sortedLocations, generatedPlaces);
    }

}
