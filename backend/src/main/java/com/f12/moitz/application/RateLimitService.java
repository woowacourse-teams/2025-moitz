package com.f12.moitz.application;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String REDIS_KEY_PREFIX = "rate_limit:";
    private static final int DAILY_LIMIT = 10;
    private static final int REFILL_AMOUNT = 10;
    private static final Duration REFILL_DURATION = Duration.ofHours(1);

    private final LettuceConnectionFactory lettuceConnectionFactory;
    private ProxyManager<byte[]> proxyManager;

    private ProxyManager<byte[]> getProxyManager() {
        if (proxyManager == null) {
            RedisClient redisClient = RedisClient.create(
                    String.format(
                            "redis://%s:%d",
                            lettuceConnectionFactory.getHostName(),
                            lettuceConnectionFactory.getPort()
                    )
            );
            ClientSideConfig clientSideConfig = ClientSideConfig.getDefault()
                    .withExpirationAfterWriteStrategy(
                            ExpirationAfterWriteStrategy
                                    .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(3))
                    );

            proxyManager = LettuceBasedProxyManager.builderFor(redisClient)
                    .withClientSideConfig(clientSideConfig)
                    .build();

        }
        return proxyManager;
    }

    public boolean tryConsume(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;

        Supplier<BucketConfiguration> configurationSupplier = () -> {
            Bandwidth limit = Bandwidth.builder()
                    .capacity(DAILY_LIMIT)
                    .refillGreedy(REFILL_AMOUNT, REFILL_DURATION)
                    .build();

            return BucketConfiguration.builder()
                    .addLimit(limit)
                    .build();
        };

        Bucket bucket = getProxyManager().builder()
                .build(key.getBytes(), configurationSupplier);

        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
        }else{
            log.debug("Request allowed for IP: {}, remaining tokens: {}",
                    clientIp, bucket.getAvailableTokens());
        }

        return consumed;
    }

    public long getAvailableTokens(String clientIp) {
        String key = REDIS_KEY_PREFIX + clientIp;

        Supplier<BucketConfiguration> configurationSupplier = () -> {
            Bandwidth bandwidth = Bandwidth.builder()
                    .capacity(DAILY_LIMIT)
                    .refillGreedy(DAILY_LIMIT, REFILL_DURATION)
                    .build();

            return BucketConfiguration.builder()
                    .addLimit(bandwidth)
                    .build();
        };

        Bucket bucket = getProxyManager().builder()
                .build(key.getBytes(), configurationSupplier);

        return bucket.getAvailableTokens();
    }


}
