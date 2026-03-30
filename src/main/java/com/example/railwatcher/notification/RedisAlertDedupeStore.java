package com.example.railwatcher.notification;

import com.example.railwatcher.config.RailwayWatcherProperties;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "railway.redis", name = "enabled", havingValue = "true")
public class RedisAlertDedupeStore implements AlertDedupeStore {

    private final StringRedisTemplate redisTemplate;
    private final RailwayWatcherProperties properties;

    public RedisAlertDedupeStore(StringRedisTemplate redisTemplate, RailwayWatcherProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Override
    public boolean markIfNew(UUID watchJobId, String dedupeKey, Duration ttl) {
        String key = properties.redis().keyPrefix() + ":" + watchJobId + ":" + dedupeKey;
        Boolean created = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(created);
    }
}
