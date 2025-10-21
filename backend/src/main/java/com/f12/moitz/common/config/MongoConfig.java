package com.f12.moitz.common.config;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer() {
        return builder -> builder
                .applyToConnectionPoolSettings(connectionPool -> connectionPool
                        .maxSize(30)
                        .minSize(5)
                        .maxConnectionIdleTime(300_000, TimeUnit.MILLISECONDS)
                        .maxConnectionLifeTime(600_000, TimeUnit.MILLISECONDS)
                        .maxWaitTime(15_000, TimeUnit.MILLISECONDS)
                )
                .applyToSocketSettings(socket -> socket
                        .connectTimeout(15_000, TimeUnit.MILLISECONDS)
                        .readTimeout(30_000, TimeUnit.MILLISECONDS)
                )
                .applyToClusterSettings(cluster -> cluster
                        .serverSelectionTimeout(10_000, TimeUnit.MILLISECONDS)
                )
                .applyToServerSettings(server -> server
                        .heartbeatFrequency(30_000, TimeUnit.MILLISECONDS)
                        .minHeartbeatFrequency(10_000, TimeUnit.MILLISECONDS)
                )
                .readPreference(ReadPreference.primaryPreferred())
                .readConcern(ReadConcern.LOCAL);
    }

}
