package com.f12.moitz.infrastructure.adapter;

import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMEND_PROMPT;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.port.AsyncPlaceRecommender;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.gpt.GptClient;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceRecommenderAsyncAdapter implements AsyncPlaceRecommender {

    private final KakaoMapClient kakaoMapClient;
    private final GoogleGeminiClient geminiClient;
    private final GptClient gptClient;

    @Async("asyncTaskExecutor")
    @Override
    public CompletableFuture<Map<Place, List<RecommendedPlace>>> recommendPlacesAsync(final List<Place> targets, final String requirement) {
        return CompletableFuture.completedFuture(recommendPlaces(targets, requirement));
    }

    @Override
    public Map<Place, List<RecommendedPlace>> recommendPlaces(final List<Place> targets, final String requirement) {
        String targetPlaces = targets.stream()
                .map(place -> String.format(
                        "%s(x=%f, y=%f)", place.getName(), place.getPoint().getX(), place.getPoint().getY()
                ))
                .collect(Collectors.joining(", "));

        // 1. 카카오 맵에서 장소 검색
        Map<Place, List<KakaoApiResponse>> searchedAllPlaces = searchPlacesWithRequirement(targets, requirement);

        // 2. 프롬프트용 데이터 포맷팅
        String formattedKakaoData = formatKakaoDataForPrompt(searchedAllPlaces);

        // 3. 프롬프트 생성
        String prompt = String.format(
                PLACE_RECOMMEND_PROMPT,
                PLACE_RECOMMENDATION_COUNT,
                targetPlaces,
                formattedKakaoData
        );

        log.debug("Generated prompt with Kakao data for requirement: {}", requirement);
        log.debug("Formatted Kakao data: {}", formattedKakaoData);

        Map<String, List<PlaceRecommendResponse>> responses = geminiClient.generateWith(prompt);
        log.debug("AI response: {}", responses);

        return targets.stream()
                .map(place -> {
                    String placeName = place.getName();
                    List<PlaceRecommendResponse> placeResponses = responses.get(placeName);

                    if (placeResponses == null) {
                        log.warn("No recommendations found for place: {}. Available keys: {}",
                                placeName, responses.keySet());
                        placeResponses = List.of();
                    }

                    return Map.entry(
                            place,
                            placeResponses.stream()
                                    .map(response -> new RecommendedPlace(
                                            response.name(),
                                            response.category(),
                                            response.walkingTime(),
                                            response.url()
                                    )).toList()
                    );
                })
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

    /**
     * 카카오 맵 데이터를 toString()으로 간단하게 포맷팅
     */
    private String formatKakaoDataForPrompt(Map<Place, List<KakaoApiResponse>> searchedAllPlaces) {
        StringBuilder sb = new StringBuilder();

        sb.append("KAKAO MAP SEARCH RESULTS:\n");
        sb.append("========================\n\n");

        searchedAllPlaces.forEach((place, kakaoResponseList) -> {
            // 역명에 "역"이 없으면 추가하여 AI가 일관된 형식으로 응답하도록 함
            String stationName = place.getName();
            if (!stationName.endsWith("역")) {
                stationName = stationName + "역";
            }

            sb.append(String.format("=== %s ===\n", stationName));
            sb.append(String.format("Station Coordinates: (%.6f, %.6f)\n",
                    place.getPoint().getX(), place.getPoint().getY()));
            sb.append("Search Results:\n");

            for (int i = 0; i < kakaoResponseList.size(); i++) {
                KakaoApiResponse response = kakaoResponseList.get(i);
                sb.append(String.format("Response %d: %s\n", i + 1, response.toString()));
            }

            sb.append("\n");
        });

        sb.append("========================\n");
        sb.append("Please analyze the above Kakao Map data and select the best places that match the user requirement.\n");
        sb.append("Focus on places that are NOT subway stations (지하철역) and are relevant to the user's needs.\n");
        sb.append("IMPORTANT: Use the exact station names shown above (with '역' suffix) in your response.\n\n");

        log.info("log = {}" , sb.toString());
        return sb.toString();
    }
}
