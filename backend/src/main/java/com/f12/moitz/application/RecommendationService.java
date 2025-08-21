package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {

    private final PlaceService placeService;
    private final PlaceRecommender placeRecommender;
    private final LocationRecommender locationRecommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;

    public RecommendationService(
            @Autowired final PlaceService placeService,
            @Qualifier("placeRecommenderAdapter") final PlaceRecommender placeRecommender,
            @Autowired final LocationRecommender locationRecommender,
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired RecommendResultRepository recommendResultRepository
    ) {
        this.placeService = placeService;
        this.placeRecommender = placeRecommender;
        this.locationRecommender = locationRecommender;
        this.routeFinder = routeFinder;
        this.recommendationMapper = recommendationMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    /*
        * /locations 유지를 위해 임시로 사용되는 추천 서비스 메소드입니다.
     */
    public com.f12.moitz.application.dto.temp.RecommendationsResponse tempRecommendLocation(final RecommendationRequest request) {
        final RecommendationsResponse result = findResultById(recommendLocation(request));
        return new com.f12.moitz.application.dto.temp.RecommendationsResponse(
                result.startingPlaces(),
                result.locations()
        );
    }

    public String recommendLocation(final RecommendationRequest request) {
        StopWatch stopWatch = new StopWatch("추천 서비스 전체");

        stopWatch.start("지역 추천");
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getKeyword();
        final List<Place> startingPlaces = placeService.findByNames(request.startingPlaceNames());

        final RecommendedLocationsResponse recommendedLocationsResponse = locationRecommender.recommendLocations(
                request.startingPlaceNames(),
                requirement
        );
        final Map<Place, ReasonAndDescription> generatedPlacesWithReason = recommendedLocationsResponse.recommendations()
                .stream()
                .collect(Collectors.toMap(
                        recommendation -> placeService.findByName(recommendation.locationName()),
                        recommendation -> new ReasonAndDescription(
                                recommendation.reason(),
                                recommendation.description()
                        )
                ));
        stopWatch.stop();

        stopWatch.start("모든 경로 찾기");
        List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();
        final Map<Place, Routes> placeRoutes = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        stopWatch.stop();

        stopWatch.start("기준 미달 경로 제거");
        final Map<Place, ReasonAndDescription> filteredPlacesWithReason = removePlacesBeyondRange(placeRoutes, generatedPlacesWithReason);
        generatedPlaces = filteredPlacesWithReason.keySet().stream().toList();
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final Map<Place, List<RecommendedPlace>> recommendedPlaces = placeRecommender.recommendPlaces(
                generatedPlaces,
                requirement
        );
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = recommendationMapper.toRecommendation(
                filteredPlacesWithReason,
                recommendedPlaces,
                placeRoutes
        );
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        return recommendResultRepository.saveAndReturnId(
                recommendationMapper.toResult(
                        startingPlaces,
                        recommendation
                )
        ).toHexString().toUpperCase();
    }

    public Map<Place, Routes> findRoutesForAllAsync(
            final List<Place> startingPlaces,
            final List<Place> generatedPlaces
    ) {
        final List<StartEndPair> allPairs = generatedPlaces.stream()
                .flatMap(endPlace -> startingPlaces.stream()
                        .map(startPlace -> new StartEndPair(startPlace, endPlace)))
                .collect(Collectors.toList());

        final List<Route> allRoutes = routeFinder.findRoutes(allPairs);

        return IntStream.range(0, allPairs.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> allPairs.get(i).end(),
                        Collectors.mapping(
                                allRoutes::get,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new Routes(entry.getValue())
                ));
    }

    private Map<Place, ReasonAndDescription> removePlacesBeyondRange(
            final Map<Place, Routes> placeRoutes,
            final Map<Place, ReasonAndDescription> generatedPlaces
    ) {
        return generatedPlaces.entrySet().stream()
                .filter(entry -> {
                    Place place = entry.getKey();
                    return placeRoutes.containsKey(place) && placeRoutes.get(place).isAcceptable();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public RecommendationsResponse findResultById(final String id) {
        final Result result = recommendResultRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 결과를 찾을 수 없습니다."));
        return recommendationMapper.toResponse(result);
    }

}
