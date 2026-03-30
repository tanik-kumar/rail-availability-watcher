package com.example.railwatcher.notification;

import java.time.Duration;
import java.util.UUID;

public interface AlertDedupeStore {

    boolean markIfNew(UUID watchJobId, String dedupeKey, Duration ttl);
}
