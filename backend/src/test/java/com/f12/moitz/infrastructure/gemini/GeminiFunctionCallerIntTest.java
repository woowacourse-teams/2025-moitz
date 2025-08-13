package com.f12.moitz.infrastructure.gemini;

import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMENDATION_COUNT;
import static com.f12.moitz.infrastructure.PromptGenerator.PLACE_RECOMMEND_PROMPT;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeminiFunctionCallerIntTest {

    @Autowired
    private GeminiFunctionCaller functionCaller;
    @Autowired
    private GoogleGeminiClient geminiClient;

    @Disabled
    @DisplayName("장소 추천 테스트")
    @Test
    void recommendPlaces() {
        // Given
        Set<Place> places = Set.of(
                new Place("잠실역", new Point(127.10023101886318, 37.51331105877401)),
                new Place("삼성역", new Point(127.06302321147605, 37.508822740225305)),
                new Place("잠실새내역", new Point(127.086314327913, 37.5116263087296))
        );

        String targetPlaces = places.stream()
                .map(place -> String.format(
                        "%s(x=%f, y=%f)", place.getName(), place.getPoint().getX(), place.getPoint().getY()
                ))
                .collect(Collectors.joining(", "));

        String requirement = "곱창";

        String prompt = String.format(PLACE_RECOMMEND_PROMPT, PLACE_RECOMMENDATION_COUNT, targetPlaces, requirement);

        // When
        Map<String, List<PlaceRecommendResponse>> recommendedPlaces = functionCaller.generateWith(prompt, geminiClient);

        // Then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedPlaces).hasSize(places.size());
            softAssertions.assertThat(recommendedPlaces.get("잠실역")).hasSize(PLACE_RECOMMENDATION_COUNT);
            softAssertions.assertThat(recommendedPlaces.get("삼성역")).hasSize(PLACE_RECOMMENDATION_COUNT);
            softAssertions.assertThat(recommendedPlaces.get("잠실새내역")).hasSize(PLACE_RECOMMENDATION_COUNT);
        });
    }

}
