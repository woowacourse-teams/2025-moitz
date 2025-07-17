package com.f12.moitz.common;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient() {
        // TODO: 연결 설정 추가 (타임 아웃 등)
        return RestClient.builder()
                .build();
    }

    @Bean
    public Client.Builder geminiClientBuilder() {
        return Client.builder();
    }

}
