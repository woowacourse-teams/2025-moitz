package com.f12.moitz.common.config;

import com.f12.moitz.domain.subway.SubwayMapPathFinder;
import com.f12.moitz.domain.subway.SubwayStation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ClientConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

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
        return geminiClientBuilder().apiKey(apiKey).build();
    }

    @Bean
    public WebClient odsayWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5))
                        .addHandlerLast(new WriteTimeoutHandler(5)));

        return WebClient.builder()
                .baseUrl("https://api.odsay.com/v1/api")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public SubwayMapPathFinder subwayMapPathFinder(@Autowired ObjectMapper objectMapper) {
        try {
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream("station-map.json");
            if (resource == null || resource.available() == 0) {
                throw new RuntimeException("station-map.json 파일을 찾을 수 없습니다. 먼저 SubwayMapBuilder.build()를 실행하여 JSON 파일을 생성해주세요.");
            }
            
            // Java 8 시간 타입 지원을 위한 모듈 등록
            objectMapper.findAndRegisterModules();
            
            TypeReference<Map<String, SubwayStation>> typeReference = new TypeReference<>() {};
            Map<String, SubwayStation> stationMap = objectMapper.readValue(resource, typeReference);
            
            if (stationMap.isEmpty()) {
                throw new RuntimeException("station-map.json에서 읽어온 데이터가 비어있습니다. JSON 파일을 다시 생성해주세요.");
            }
            
            return new SubwayMapPathFinder(stationMap);
        } catch (IOException e) {
            throw new RuntimeException("station-map.json 파일 읽기에 실패했습니다.", e);
        }
    }

}
