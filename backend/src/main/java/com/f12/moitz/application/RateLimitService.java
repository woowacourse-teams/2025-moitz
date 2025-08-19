package com.f12.moitz.application;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String REDIS_KEY_PREFIX = "rate_limit:";
    private static final int BUCKET_CAPACITY = 100;
    private static final int REFILL_AMOUNT = 50;
    private static final Duration REFILL_DURATION = Duration.ofHours(1);

    private final Supplier<BucketConfiguration> configurationSupplier = () -> {
        Bandwidth limit = Bandwidth.builder()
                .capacity(BUCKET_CAPACITY)
                .refillGreedy(REFILL_AMOUNT, REFILL_DURATION)
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    };
    private final LettuceConnectionFactory lettuceConnectionFactory;
    private ProxyManager<byte[]> proxyManager;

    private ProxyManager<byte[]> getProxyManager() {
        if (proxyManager == null) {
            final RedisClient redisClient = RedisClient.create(
                    String.format(
                            "redis://%s:%d",
                            lettuceConnectionFactory.getHostName(),
                            lettuceConnectionFactory.getPort()
                    )
            );
            final ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
                    .withExpirationAfterWriteStrategy(
                            ExpirationAfterWriteStrategy
                                    .basedOnTimeForRefillingBucketUpToMax(Duration.ofDays(10))
                    );

            proxyManager = LettuceBasedProxyManager.builderFor(redisClient)
                    .withClientSideConfig(clientSideConfig)
                    .build();

        }
        return proxyManager;
    }

    public boolean tryConsume(String clientIp) {
        final String key = REDIS_KEY_PREFIX + clientIp;

        final Bucket bucket = getProxyManager().builder()
                .build(key.getBytes(), configurationSupplier);

        final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        final boolean isConsumed = probe.isConsumed();

        if (!isConsumed) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
        } else {
            log.debug("Request allowed for IP: {}, remaining tokens: {}",
                    clientIp, bucket.getAvailableTokens());
        }

        return isConsumed;
    }

    public long getAvailableTokens(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;

        Bucket bucket = getProxyManager().builder()
                .build(key.getBytes(), configurationSupplier);

        return bucket.getAvailableTokens();
    }

}
