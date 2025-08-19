package com.f12.moitz.infrastructure.client.perplexity;

import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.f12.moitz.common.error.exception.ExternalApiErrorCode;
import com.f12.moitz.common.error.exception.ExternalApiException;
import com.f12.moitz.common.error.exception.RetryableApiException;
import com.f12.moitz.infrastructure.client.perplexity.dto.PerplexityRequest;
import com.f12.moitz.infrastructure.client.perplexity.dto.PerplexityResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
@Component
public class PerplexityClient {

    private final WebClient perplexityWebClient;
    private final ObjectMapper objectMapper;

    public RecommendedLocationResponse generateResponse(
            final List<String> stationNames,
            final String requirement
    ) {
        try {
            final String content = generateContent(stationNames, requirement).choices().getFirst().message().content();
            return objectMapper.readValue(content, RecommendedLocationResponse.class);
        } catch (IOException e) {
            throw new ExternalApiException(ExternalApiErrorCode.INVALID_PERPLEXITY_API_RESPONSE);
        }
    }

    private PerplexityResponse generateContent(
            final List<String> stationNames,
            final String requirement
    ) {
        final String stations = String.join(", ", stationNames);

        final String systemPrompt = """
            당신은 서울의 최적의 모임 장소를 추천하는 AI 비서입니다.
            당신의 목표는 모든 출발지에서 지하철 이동 시간이 비슷하고, 사용자의 추가 조건을 만족하는 장소를 추천하는 것입니다.
            사용자는 20대 초반에서 30대 초반으로 서울 지하철을 이용하며,
            추천 장소는 서울 메트로시티의 지하철 역이여야 합니다.
            반드시 아래 JSON Schema 조건을 준수하여 응답하세요.
            Schema를 만족하지 않으면 잘못된 응답입니다.
        """;

        final String userPrompt = String.format("""
            ## 핵심 임무:
            1.  **서울 지하철역 범위**: 출발지와 추천 장소는 모두 서울 지하철역이어야 합니다.
            2.  **유사한 이동 시간**: 추천 장소까지의 이동 시간 편차(가장 오래 걸리는 시간 - 가장 적게 걸리는 시간)는 15분 이내여야 합니다.
            3.  **편의 시설**: 추천 장소는 지하철역 근처에 식당/카페가 많아야 하며, 아래의 사용자 추가 조건을 반드시 만족해야 합니다.
            4.  **출발지 제외**: 추천 장소는 주어진 출발지 중 하나여서는 안 됩니다.
            5.  **적절한 추천 이유**: 장소와 함께 위 조건을 고려한 20자 이내의 추천 이유를 제시하세요. (끝에 이모지 하나를 사용해주세요.)
        
            출발지 목록: %s
            추천 장소 개수: 5
            추가 조건: %s
        
            ## 응답 형식:
            <EXAMPLE>
            {
                "recommendations": [
                    {
                        "locationName": "서울역",
                        "reason": "주변 상권이 잘 발달되어 있고, 이동 소요 시간이 전체적으로 짧은 편이에요 :smile:"
                    },
                    {
                        "locationName": "강남역",
                        "reason": "다양한 음식점과 카페가 있어요 :coffee:"
                    }
                ]
            }
            </EXAMPLE>
        """, stations, requirement);

        final PerplexityRequest requestPayload = new PerplexityRequest(
                "sonar-pro",
                List.of(
                        new PerplexityRequest.Message("system", systemPrompt),
                        new PerplexityRequest.Message("user", userPrompt)
                ),
                Map.of("type", "json_schema", "json_schema", Map.of(
                        "schema", getSchema()
                ))
        );

        return perplexityWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestPayload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .bodyToMono(PerplexityResponse.class)
                .retryWhen(Retry.max(1)
                        .filter(throwable -> throwable instanceof RetryableApiException || throwable instanceof TimeoutException)
                        .doBeforeRetry(retrySignal ->
                                log.warn(
                                        "API 호출 실패. 재시도 #{} 시작. 실패 원인: {}",
                                        retrySignal.totalRetries() + 1,
                                        retrySignal.failure().getMessage()
                                )
                        )
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new ExternalApiException(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNRESPONSIVE)
                        )
                )
                .block();
    }

    private Mono<? extends Throwable> handleApiError(final ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("No Error Body")
                .flatMap(errorBody -> switch (response.statusCode().value()) {
                    case 401 -> Mono.error(new ExternalApiException(ExternalApiErrorCode.INVALID_PERPLEXITY_API_KEY));
                    case 402 -> Mono.error(new ExternalApiException(ExternalApiErrorCode.EXCEEDED_PERPLEXITY_API_TOKEN_QUOTA));
                    case 400 -> Mono.error(new ExternalApiException(ExternalApiErrorCode.INVALID_PERPLEXITY_API_RESPONSE));
                    case 429 -> Mono.error(new RetryableApiException(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNAVAILABLE));
                    default -> {
                        if (response.statusCode().is5xxServerError()) {
                            yield Mono.error(new RetryableApiException(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNAVAILABLE));
                        }
                        yield Mono.error(new ExternalApiException(ExternalApiErrorCode.PERPLEXITY_API_SERVER_UNRESPONSIVE));
                    }
                });
    }

    private Map<String, Object> getSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "description", "추천 지하철역 리스트",
                                "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "locationName", Map.of("type", "string", "description", "추천 장소 이름"),
                                                "reason", Map.of("type", "string", "description", "20자 이내 추천 이유 + 이모지 ex) 주변 상권이 잘 발달되어 있고, 이동 소요 시간이 전체적으로 짧은 편이에요 :smile:", "maxLength", 20)
                                        ),
                                        "required", List.of("locationName", "reason")
                                ),
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }
}
