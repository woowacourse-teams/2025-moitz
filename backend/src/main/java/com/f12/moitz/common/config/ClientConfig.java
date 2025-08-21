package com.f12.moitz.common.config;

import com.f12.moitz.application.PlaceService;
import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayMapPathFinder;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.SubwayMapBuilder;
import com.google.genai.Client;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class ClientConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${perplexity.api.key}")
    private String perplexityApiKey;

    @Bean
    public RestClient kakaoRestClient() {
        return restClientBuilder()
                .baseUrl("https://dapi.kakao.com/v2/local/search")
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient odsayRestClient() {
        return restClientBuilder()
                .baseUrl("https://api.odsay.com/v1/api")
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient openRestClient() {
        return restClientBuilder()
                .baseUrl("https://apis.data.go.kr/B553766/path")
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return requestFactory;
    }

    private RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public Client.Builder geminiClientBuilder() {
        return Client.builder();
    }

    @Bean
    public Client geminiClient() {
        return geminiClientBuilder().apiKey(geminiApiKey).build();
    }

    @Bean
    public WebClient odsayWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.odsay.com/v1/api")
                .clientConnector(new ReactorClientHttpConnector(httpClient(5)))
                .build();
    }

    @Bean
    public WebClient perplexityWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.perplexity.ai")
                .clientConnector(new ReactorClientHttpConnector(httpClient(20)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + perplexityApiKey)
                .build();
    }

    private HttpClient httpClient(final int seconds) {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(seconds))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(seconds))
                        .addHandlerLast(new WriteTimeoutHandler(seconds)));
    }

    @Bean
    public SubwayMapPathFinder subwayMapPathFinder(
            @Autowired SubwayStationRepository stationRepository,
            @Autowired SubwayMapBuilder subwayMapBuilder,
            @Autowired PlaceService placeService
    ) {
        log.info("SubwayMapPathFinder 초기화 시작");
        try {
            Map<String, SubwayStation> stationMap = stationRepository.findAllAsMap();
            if (stationMap == null || stationMap.isEmpty()) {
                log.info("MongoDB에 데이터가 없습니다. CSV에서 자동 빌드를 시작합니다...");
                stationMap = subwayMapBuilder.buildAndSaveToMongo();
                log.info("자동 빌드 완료. {}개 역 저장됨", stationMap.size());

                int saved = placeService.saveIfAbsent(new ArrayList<>(stationMap.keySet()));
                log.info("Place 초기화 완료. {}개 추가되었습니다.", saved);
            }
            log.info("SubwayMapPathFinder 초기화 완료. 총 {}개 역", stationMap.size());

            return new SubwayMapPathFinder(stationMap);
        } catch (Exception e) {
            log.error("SubwayMapPathFinder 초기화 실패", e);
            throw new RuntimeException("SubwayMapPathFinder 초기화 실패: " + e.getMessage(), e);
        }
    }

}
