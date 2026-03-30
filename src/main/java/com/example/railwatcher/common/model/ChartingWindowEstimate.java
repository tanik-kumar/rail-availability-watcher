package com.example.railwatcher.common.model;

import java.time.LocalDateTime;

public record ChartingWindowEstimate(
        LocalDateTime originChartTime,
        LocalDateTime remoteChartTime,
        LocalDateTime activeChartTime,
        LocalDateTime burstPollingStart,
        LocalDateTime elevatedPollingStart,
        LocalDateTime stopPollingAfter,
        boolean remoteBoarding
) {
}
