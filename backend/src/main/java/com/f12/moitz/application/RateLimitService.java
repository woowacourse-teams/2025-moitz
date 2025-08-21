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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private static final int BUCKET_CAPACITY = 1000;
    private static final int REFILL_AMOUNT = 1000;
    private static final Duration REFILL_DURATION = Duration.ofHours(1);

    private final Supplier<BucketConfiguration> configurationSupplier = () -> {
        final Bandwidth limit = Bandwidth.builder()
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
                                    .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))
                    );

            proxyManager = LettuceBasedProxyManager.builderFor(redisClient)
                    .withClientSideConfig(clientSideConfig)
                    .build();

        }
        return proxyManager;
    }

    public ConsumptionProbe tryConsume(final String clientIp, final String userAgent) throws NoSuchAlgorithmException {
        final String hashedString = getHashedString(clientIp, userAgent);
        final String key = REDIS_KEY_PREFIX + hashedString;

        final Bucket bucket = getProxyManager().builder()
                .build(key.getBytes(), configurationSupplier);

        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private String getHashedString(final String clientIp, final String userAgent) throws NoSuchAlgorithmException {
        final String ipAndUserAgent = clientIp + userAgent;
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] encodedHash = digest.digest(ipAndUserAgent.getBytes(StandardCharsets.UTF_8));

        final StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            final String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
