package com.f12.moitz.infrastructure.adapter;

import static com.f12.moitz.infrastructure.PromptGenerator.FORMAT_SINGLE_PLACE_TO_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_FILTER_PROMPT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.AsyncPlaceRecommender;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.kakao.KakaoMapClient;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;
import com.f12.moitz.infrastructure.client.kakao.dto.SearchPlacesRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiPlaceRecommenderAsyncAdapter implements AsyncPlaceRecommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;

    @Async("asyncTaskExecutor")
    @Override
    public CompletableFuture<Map<Place, List<RecommendedPlace>>> recommendPlacesAsync(
            final List<Place> targets,
            final String requirement
    ) {
        return recommendPlaces(targets, requirement).toFuture();
    }

    private Mono<Map<Place, List<RecommendedPlace>>> recommendPlaces(final List<Place> targets, final String requirement) {
        final Map<Place, List<KakaoApiResponse>> searchedAllPlaces = searchPlacesWithRequirement(targets, requirement);

        return Flux.fromIterable(searchedAllPlaces.entrySet())
                .flatMap(entry -> processPlaceFilteringAsync(entry.getKey(), entry.getValue(), requirement), 5)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private Map<Place, List<KakaoApiResponse>> searchPlacesWithRequirement(
            final List<Place> targets,
            final String requirement
    ) {
        return targets.stream()
                .collect(Collectors.toMap(
                        place -> place,
                        place -> {
                            KakaoApiResponse response = kakaoMapClient.searchPlacesBy(
                                    new SearchPlacesRequest(
                                            requirement,
                                            place.getPoint().getX(),
                                            place.getPoint().getY(),
                                            1000
                                    )
                            );
                            return List.of(response);
                        }
                ));
    }

    private Mono<Map.Entry<Place, List<RecommendedPlace>>> processPlaceFilteringAsync(
            final Place place,
            final List<KakaoApiResponse> kakaoResponses,
            final String requirement
    ) {
        return Mono.fromCallable(() -> {
                    log.info("Gemini 블로킹 작업 시작 (스레드: {})", Thread.currentThread().getName());
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
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error filtering places for: {}", place.getName(), e))
                .onErrorResume(e -> Mono.empty());
    }


}
