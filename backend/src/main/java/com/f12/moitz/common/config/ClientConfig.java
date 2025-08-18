package com.f12.moitz.common.config;

import com.google.genai.Client;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
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

@Configuration
public class ClientConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${perplexity.api.key}")
    private String perplexityApiKey;

    @Bean
    public Client geminiClient() {
        return geminiClientBuilder().apiKey(geminiApiKey).build();
    }

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
    public WebClient odsayWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.odsay.com/v1/api")
                .clientConnector(new ReactorClientHttpConnector(httpClient(5)))
                .build();
    }

    private HttpClient httpClient(final int seconds) {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(seconds))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(seconds))
                        .addHandlerLast(new WriteTimeoutHandler(seconds)));
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

}
