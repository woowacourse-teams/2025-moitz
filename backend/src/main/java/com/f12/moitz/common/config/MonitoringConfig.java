package com.f12.moitz.common.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

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

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry(
            final CloudWatchConfig config,
            final Clock clock,
            final CloudWatchAsyncClient client
    ) {
        return new CloudWatchMeterRegistry(config, clock, client);
    }

    @Bean
    public MeterFilter meterFilter() {
        return MeterFilter.denyUnless(id -> id.getName().startsWith("http"));
    }

}
