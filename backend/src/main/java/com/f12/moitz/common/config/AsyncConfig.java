package com.f12.moitz.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "placeRecommendationExecutor")
    public Executor placeRecommendationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);           // 기본 스레드 수
        executor.setMaxPoolSize(8);           // 최대 스레드 수
        executor.setQueueCapacity(100);        // 큐 용량
        executor.setKeepAliveSeconds(60);      // 유휴 스레드 생존 시간
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}