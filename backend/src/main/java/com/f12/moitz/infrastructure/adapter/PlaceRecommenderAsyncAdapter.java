package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.AsyncPlaceRecommender;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapAsyncClient;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceRecommenderAsyncAdapter implements AsyncPlaceRecommender {

    private final KakaoMapAsyncClient kakaoMapAsyncClient;

    public Mono<Map<Place, CategorizedRecommendedPlaces>> recommendPlacesAsync(
            final List<Place> targetPlaces,
            final List<RecommendCondition> requirements
    ) {
        return searchPlacesWithRequirementAsync(targetPlaces, requirements)
                .map(this::buildCategorizedRecommendedPlaces);
    }

    private Map<Place, CategorizedRecommendedPlaces> buildCategorizedRecommendedPlaces(
            final Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>> searchResults
    ) {
        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new CategorizedRecommendedPlaces(
                                buildCategoryMap(entry.getValue())
                        )
                ));
    }

    private Map<RecommendCondition, List<RecommendedPlace>> buildCategoryMap(
            final Map<RecommendCondition, List<KakaoApiResponse>> categoryResponses
    ) {
        return categoryResponses.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().stream()
                                .flatMap(resp -> resp.documents().stream())
                                .map(this::toRecommendedPlace)
                                .toList()
                ));
    }

    private RecommendedPlace toRecommendedPlace(
            final DocumentResponse document
    ) {
        return new RecommendedPlace(
                document.placeName(),
                new Point(
                        Double.parseDouble(document.x()),
                        Double.parseDouble(document.y())
                ),
                parseCategoryName(document.categoryName()),
                calculateWalkingTime(Integer.parseInt(document.distance())),
                document.placeUrl(),
                document.imageUrl()
        );
    }

    private Mono<Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>>> searchPlacesWithRequirementAsync(
            final List<Place> targetPlaces,
            final List<RecommendCondition> requirements
    ) {
        return Flux.fromIterable(targetPlaces)
                .flatMap(place -> searchRequirementsForPlaceAsync(place, requirements)
                        .map(requirementMap -> Map.entry(place, requirementMap))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private Mono<Map<RecommendCondition, List<KakaoApiResponse>>> searchRequirementsForPlaceAsync(
            final Place place,
            final List<RecommendCondition> requirements
    ) {
        return Flux.fromIterable(requirements)
                .flatMap(condition -> searchKeywordsForConditionAsync(condition, place))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue, HashMap::new);
    }

    private Mono<Map.Entry<RecommendCondition, List<KakaoApiResponse>>> searchKeywordsForConditionAsync(
            final RecommendCondition condition,
            final Place place
    ) {
        return Flux.fromIterable(condition.getKeywords())
                .flatMap(keyword -> kakaoMapAsyncClient.searchPlacesByAsync(
                        new SearchPlacesLimitQuantityRequest(
                                keyword,
                                place.getName(),
                                place.getPoint().getX(),
                                place.getPoint().getY(),
                                800,
                                3
                        )
                )
                .retry(2)
                .onErrorMap(e -> new ExternalApiException(
                        ExternalApiErrorCode.INVALID_KAKAO_MAP_API_RESPONSE,
                        "조건 '" + condition.getTitle() + "'의 키워드 '" + keyword +
                        "'에 대한 검색이 실패했습니다. / " + e.getMessage()
                ))
                )
                .collectList()
                .map(responses -> Map.entry(condition, responses));
    }

    private int calculateWalkingTime(final int distance) {
        return Math.toIntExact(Math.round((double) distance / 100 * 1.5));
    }

    private String parseCategoryName(final String categoryName) {
        final String regex = ">";
        if (!categoryName.contains(regex)) {
            return categoryName;
        }
        final List<String> tokens = Arrays.stream(categoryName.split(regex))
                .map(String::trim)
                .toList();
        return tokens.getLast();
    }

}
