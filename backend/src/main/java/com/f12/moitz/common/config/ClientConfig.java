package com.f12.moitz.common.config;

import com.google.genai.Client;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

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

}
