package com.f12.moitz.infrastructure.adapter;

import static com.f12.moitz.infrastructure.PromptGenerator.FORMAT_SINGLE_PLACE_TO_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_FILTER_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiPlaceRecommenderAdapter implements PlaceRecommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;

    @Override
    public Map<Place, List<RecommendedPlace>> recommendPlaces(final List<Place> targets, final String requirement) {
        Map<Place, List<KakaoApiResponse>> searchedAllPlaces = searchPlacesWithRequirement(targets, requirement);

        return searchedAllPlaces.entrySet().stream()
                .map(entry -> processPlaceFiltering(entry.getKey(), entry.getValue(), requirement))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Place, List<KakaoApiResponse>> searchPlacesWithRequirement(final List<Place> targets, final String requirement) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
                            KakaoApiResponse response = kakaoMapClient.searchPlacesBy(
                                    new SearchPlacesRequest(
                                            requirement,
                                            place.getPoint().getX(),
                                            place.getPoint().getY(),
                                            800
                                    )
                            );
                            return List.of(response);
                        }
                ));
    }

    public Entry<Place, List<RecommendedPlace>> processPlaceFiltering(
            final Place place,
            final List<KakaoApiResponse> kakaoResponses,
            final String requirement
    ) {
        try {
            String formattedKakaoData = FORMAT_SINGLE_PLACE_TO_PROMPT(place, kakaoResponses);

            String prompt = String.format(
                    PLACE_FILTER_PROMPT,
                    place.getName(),
                    PLACE_RECOMMENDATION_COUNT,
                    place.getName(),
                    place.getPoint().getX(),
                    place.getPoint().getY(),
                    requirement,
                    formattedKakaoData,
                    PLACE_RECOMMENDATION_COUNT
            );

            List<PlaceRecommendResponse> filteredResponses = geminiClient.generateWith(prompt);

            List<RecommendedPlace> recommendedPlaces = filteredResponses.stream()
                    .map(response -> new RecommendedPlace(
                            response.name(),
                            response.category(),
                            response.walkingTime(),
                            response.url()
                    ))
                    .toList();

            return Map.entry(place, recommendedPlaces);
        } catch (Exception e) {
            log.error("Error filtering places for: {}", place.getName(), e);
            throw new RetryableApiException(ExternalApiErrorCode.INVALID_ODSAY_API_RESPONSE);
        }
    }

}
