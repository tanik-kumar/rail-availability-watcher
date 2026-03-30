package com.example.railwatcher.service;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.config.RailwayWatcherProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProviderRateLimiter {

    private final Clock clock;
    private final RailwayWatcherProperties properties;
    private final Map<ProviderType, Deque<Instant>> requestLog = new EnumMap<>(ProviderType.class);

    public ProviderRateLimiter(Clock clock, RailwayWatcherProperties properties) {
        this.clock = clock;
        this.properties = properties;
        for (ProviderType providerType : ProviderType.values()) {
            requestLog.put(providerType, new ArrayDeque<>());
        }
    }

    public synchronized Optional<Duration> reserve(ProviderType providerType) {
        int limit = properties.provider().permitsPerMinute();
        if (limit <= 0) {
            return Optional.empty();
        }
        Instant now = Instant.now(clock);
        Deque<Instant> timestamps = requestLog.get(providerType);
        Instant cutoff = now.minusSeconds(60);
        while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
            timestamps.removeFirst();
        }
        if (timestamps.size() >= limit) {
            Instant oldest = timestamps.peekFirst();
            return Optional.of(Duration.between(now, oldest.plusSeconds(60)));
        }
        timestamps.addLast(now);
        return Optional.empty();
    }
}
