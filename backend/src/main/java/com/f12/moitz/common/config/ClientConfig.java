package com.f12.moitz.common.config;

import com.google.genai.Client;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
    @Profile("!load-test")
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

    @Bean("kakaoRestClient")
    @Profile("load-test")
    public RestClient mockKakaoRestClient() {
        return restClientBuilder()
                .baseUrl("http://3.34.130.156:8081/mock/kakaomap")
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    @Bean
    @Profile("load-test")
    public RestClient mockGeminiRestClient() {
        return restClientBuilder()
                .baseUrl("http://3.34.130.156:8081/mock/gemini")
                .requestFactory(simpleClientHttpRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));
        return requestFactory;
    }

    private RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Profile("!load-test")
    public Client.Builder geminiClientBuilder() {
        return Client.builder();
    }

    @Bean
    @Profile("!load-test")
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

}
