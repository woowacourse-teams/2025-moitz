package com.f12.moitz;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.gemini.GeminiRecommendPlaceClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeminiRecommendedPlaceClientTest {

    @Autowired
    private GeminiRecommendPlaceClient geminiClient;

    @Disabled
    @DisplayName("장소 추천 테스트")
    @Test
    void recommendPlaces() {
        //given
        Set<Place> places = Set.of(
                new Place("잠실역",new Point(127.10023101886318,37.51331105877401)),
                new Place("삼성역",new Point(127.06302321147605,37.508822740225305)),
                new Place("잠실새내역",new Point(127.086314327913,37.5116263087296))
                );
        String requirement = "곱창";
        //when & then
        geminiClient.generateWithParallelFunctionCalling(places,requirement);
    }
}
