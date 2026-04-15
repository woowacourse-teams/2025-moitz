package com.f12.moitz.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.infrastructure.client.gemini.GoogleGeminiClient;
import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import com.f12.moitz.infrastructure.client.perplexity.PerplexityClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationReasonGeneratorAdapterTest {

    @Mock
    private GoogleGeminiClient googleGeminiClient;

    @Mock
    private PerplexityClient perplexityClient;

    private final CircuitBreaker geminiBreaker = CircuitBreaker.ofDefaults("gemini");
    private final CircuitBreaker geminiRetryableBreaker = CircuitBreaker.ofDefaults("geminiRetryable");

    @Test
    @DisplayName("Gemini 호출 실패 시 Perplexity fallback 결과를 사용한다")
    void generateReasons_UsesPerplexityFallbackWhenGeminiFails() {
        final LocationReasonGeneratorAdapter adapter = new LocationReasonGeneratorAdapter(
                googleGeminiClient,
                perplexityClient,
                geminiBreaker,
                geminiRetryableBreaker
        );
        final List<String> startingPlaces = List.of("강남역", "합정역");
        final List<String> selectedPlaces = List.of("서울역");
        final List<RecommendCondition> requirements = List.of(RecommendCondition.CAFE);

        given(googleGeminiClient.generateReasonsForSelectedLocations(
                startingPlaces,
                selectedPlaces,
                List.of("카페")
        )).willThrow(new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE));
        given(perplexityClient.generateReasonsForSelectedLocations(
                startingPlaces,
                selectedPlaces,
                List.of("카페")
        )).willReturn(new RecommendedLocationsResponse(List.of(
                new RecommendedLocationResponse("서울역", "상권과 접근성이 균형적입니다.", "균형 잡힌 선택 📍")
        )));

        final Map<String, ReasonAndDescription> result = adapter.generateReasons(
                startingPlaces,
                selectedPlaces,
                requirements
        );

        assertThat(result.get("서울역"))
                .extracting(ReasonAndDescription::reason, ReasonAndDescription::description)
                .containsExactly("상권과 접근성이 균형적입니다.", "균형 잡힌 선택 📍");
    }

    @Test
    @DisplayName("Gemini와 Perplexity가 모두 실패하면 고정 fallback 문구를 사용한다")
    void generateReasons_UsesFixedFallbackWhenAllApisFail() {
        final LocationReasonGeneratorAdapter adapter = new LocationReasonGeneratorAdapter(
                googleGeminiClient,
                perplexityClient,
                geminiBreaker,
                geminiRetryableBreaker
        );
        final List<String> startingPlaces = List.of("강남역", "합정역");
        final List<String> selectedPlaces = List.of("서울역");
        final List<RecommendCondition> requirements = List.of(RecommendCondition.CAFE);

        given(googleGeminiClient.generateReasonsForSelectedLocations(
                startingPlaces,
                selectedPlaces,
                List.of("카페")
        )).willThrow(new ExternalApiException(ExternalApiErrorCode.INVALID_GEMINI_API_RESPONSE));
        given(perplexityClient.generateReasonsForSelectedLocations(
                startingPlaces,
                selectedPlaces,
                List.of("카페")
        )).willThrow(new ExternalApiException(ExternalApiErrorCode.INVALID_PERPLEXITY_API_RESPONSE));

        final Map<String, ReasonAndDescription> result = adapter.generateReasons(
                startingPlaces,
                selectedPlaces,
                requirements
        );

        assertThat(result.get("서울역"))
                .extracting(ReasonAndDescription::reason, ReasonAndDescription::description)
                .containsExactly(
                        "이동시간과 환승 부담을 함께 고려했을 때 균형이 좋은 만남 장소입니다.",
                        "공평한 만남 장소 📍"
                );
    }
}
