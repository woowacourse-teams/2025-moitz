package com.f12.moitz.common.config;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class LoadTestClientConfig {

    @Bean("mockKaKaoRestClient")
    public RestClient mockKakaoRestClient() {
        return restClientBuilder()
                .baseUrl("http://3.34.130.156:8081/mock/kakaomap")
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

}
