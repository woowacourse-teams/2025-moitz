package com.f12.moitz.infrastructure.adapter.place;

import com.f12.moitz.application.port.place.PlaceRecommender;
import com.f12.moitz.application.port.place.PlaceRecommendationCriteria;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.RecommendedPlace;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapAsyncClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.DocumentResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesLimitQuantityRequest;
import com.f12.moitz.infrastructure.utils.KakaoPlaceMapper;
import java.util.ArrayList;
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
public class PlaceRecommenderParallelAdapter implements PlaceRecommender {

    private final KakaoMapAsyncClient kakaoMapAsyncClient;
    private final KakaoPlaceMapper kakaoPlaceMapper;

    @Override
    public Map<Place, RecommendedPlaces> recommendPlaces(
            final List<Place> targetPlaces,
            final PlaceRecommendationCriteria criteria
    ) {
        return recommendPlacesAsync(targetPlaces, criteria)
                .block();
    }

    private Mono<Map<Place, RecommendedPlaces>> recommendPlacesAsync(
            final List<Place> targetPlaces,
            final PlaceRecommendationCriteria criteria
    ) {
        return searchPlacesWithRequirementAsync(targetPlaces, criteria)
                .map(searchResults -> buildRecommendedPlaces(searchResults, criteria.limitPerCondition()));
    }

    private Map<Place, RecommendedPlaces> buildRecommendedPlaces(
            final Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>> searchResults,
            final int limitPerCondition
    ) {
        return searchResults.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new RecommendedPlaces(
                                buildCategoryMap(entry.getValue(), limitPerCondition)
                        )
                ));
    }

    private Map<RecommendCondition, List<RecommendedPlace>> buildCategoryMap(
            final Map<RecommendCondition, List<KakaoApiResponse>> categoryResponses,
            final int limitPerCondition
    ) {
        return categoryResponses.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> interleaveDocuments(entry.getValue(), limitPerCondition).stream()
                                .map(kakaoPlaceMapper::toRecommendedPlace)
                                .toList()
                ));
    }

    private List<DocumentResponse> interleaveDocuments(
            final List<KakaoApiResponse> responses,
            final int limitPerCondition
    ) {
        final List<List<DocumentResponse>> documentsByKeyword = responses.stream()
                .map(this::getDocuments)
                .toList();
        final List<DocumentResponse> interleavedDocuments = new ArrayList<>();

        for (int index = 0; interleavedDocuments.size() < limitPerCondition; index++) {
            boolean hasNextDocument = false;

            for (List<DocumentResponse> documents : documentsByKeyword) {
                if (index < documents.size()) {
                    interleavedDocuments.add(documents.get(index));
                    hasNextDocument = true;
                }
                if (interleavedDocuments.size() == limitPerCondition) {
                    return interleavedDocuments;
                }
            }

            if (!hasNextDocument) {
                return interleavedDocuments;
            }
        }

        return interleavedDocuments;
    }

    private List<DocumentResponse> getDocuments(final KakaoApiResponse response) {
        if (response.documents() == null) {
            return List.of();
        }
        return response.documents();
    }

    private Mono<Map<Place, Map<RecommendCondition, List<KakaoApiResponse>>>> searchPlacesWithRequirementAsync(
            final List<Place> targetPlaces,
            final PlaceRecommendationCriteria criteria
    ) {
        return Flux.fromIterable(targetPlaces)
                .flatMap(place -> searchRequirementsForPlaceAsync(place, criteria)
                        .map(requirementMap -> Map.entry(place, requirementMap))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private Mono<Map<RecommendCondition, List<KakaoApiResponse>>> searchRequirementsForPlaceAsync(
            final Place place,
            final PlaceRecommendationCriteria criteria
    ) {
        return Flux.fromIterable(criteria.conditions())
                .flatMap(condition -> searchKeywordsForConditionAsync(condition, place, criteria.limitPerCondition()))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue, HashMap::new);
    }

    private Mono<Map.Entry<RecommendCondition, List<KakaoApiResponse>>> searchKeywordsForConditionAsync(
            final RecommendCondition condition,
            final Place place,
            final int limitPerCondition
    ) {
        return Flux.fromIterable(condition.getKeywords())
                .flatMapSequential(keyword -> kakaoMapAsyncClient.searchPlacesByAsync(
                        new SearchPlacesLimitQuantityRequest(
                                keyword,
                                place.getName(),
                                place.getPoint().getX(),
                                place.getPoint().getY(),
                                800,
                                limitPerCondition
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

}
