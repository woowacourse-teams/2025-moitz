package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.gemini.dto.LocationNameAndReason;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.kakao.KakaoMapClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationRecommenderAdapter implements LocationRecommender {

    private final GoogleGeminiClient geminiClient;
    private final KakaoMapClient kakaoMapClient;

    @Override
    public Map<Place, String> recommendLocations(final List<String> startPlaceNames, final String condition) {
        final RecommendedLocationResponse recommendedLocationResponse = geminiClient.generateResponse(
                startPlaceNames,
                condition
        );

        return recommendedLocationResponse.recommendations().stream()
                .collect(Collectors.toMap(
                        recommendation -> new Place(
                                recommendation.locationName(),
                                kakaoMapClient.searchPointBy(recommendation.locationName())
                        ),
                        LocationNameAndReason::reason
                ));
    }

}
