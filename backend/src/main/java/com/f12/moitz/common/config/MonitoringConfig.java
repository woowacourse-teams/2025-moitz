package com.f12.moitz.common.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MonitoringConfig {

    @Bean
    public CloudWatchConfig cloudWatchConfig() {
        return new CloudWatchConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String namespace() {
                return "Moitz2025Dev";
            }

            @Override
            public Duration step() {
                return Duration.ofMinutes(1);
            }
        };
    }

}
