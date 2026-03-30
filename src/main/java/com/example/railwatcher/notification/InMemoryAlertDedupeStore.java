package com.example.railwatcher.notification;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConditionalOnProperty(prefix = "railway.redis", name = "enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryAlertDedupeStore implements AlertDedupeStore {

    private final Clock clock;
    private final Map<String, Instant> entries = new ConcurrentHashMap<>();

    public InMemoryAlertDedupeStore(Clock clock) {
        this.clock = clock;
    }

    @Override
    public boolean markIfNew(UUID watchJobId, String dedupeKey, Duration ttl) {
        Instant now = Instant.now(clock);
        String key = watchJobId + ":" + dedupeKey;
        Instant expiresAt = entries.get(key);
        if (expiresAt != null && expiresAt.isAfter(now)) {
            return false;
        }
        entries.put(key, now.plus(ttl));
        return true;
    }
}
