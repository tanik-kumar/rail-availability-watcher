package com.example.railwatcher.common.model;

import java.time.Duration;

public record PollingDecision(
        Duration nextInterval,
        String reason
) {
}
