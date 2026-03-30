package com.example.railwatcher.service;

import com.example.railwatcher.common.model.ChartingWindowEstimate;
import com.example.railwatcher.common.model.PollingDecision;
import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class ChartingWindowEstimator {

    private final Clock clock;
    private final RailwayWatcherProperties properties;

    public ChartingWindowEstimator(Clock clock, RailwayWatcherProperties properties) {
        this.clock = clock;
        this.properties = properties;
    }

    public ChartingWindowEstimate estimate(WatchJobEntity job) {
        return estimate(WatchJobMapper.toDomain(job));
    }

    public ChartingWindowEstimate estimate(WatchJob job) {
        LocalDateTime originChartTime = job.originDepartureTime().minus(properties.charting().originLeadTime());
        boolean remoteBoarding = job.boardingDepartureTime() != null
                && !job.boardingStation().equalsIgnoreCase(job.sourceStation());
        LocalDateTime remoteChartTime = remoteBoarding
                ? job.boardingDepartureTime().minus(properties.charting().remoteLeadTime())
                : null;
        LocalDateTime now = OffsetDateTime.now(clock).toLocalDateTime();
        LocalDateTime activeChart = remoteChartTime == null || now.isBefore(originChartTime)
                ? originChartTime
                : remoteChartTime;

        return new ChartingWindowEstimate(
                originChartTime,
                remoteChartTime,
                activeChart,
                activeChart.minus(properties.charting().burstWindow()),
                activeChart.minus(properties.charting().elevatedWindow()),
                (job.boardingDepartureTime() != null ? job.boardingDepartureTime() : job.originDepartureTime())
                        .plus(properties.polling().departureGrace()),
                remoteBoarding
        );
    }

    public PollingDecision nextPollingDecision(WatchJobEntity job) {
        return nextPollingDecision(WatchJobMapper.toDomain(job));
    }

    public PollingDecision nextPollingDecision(WatchJob job) {
        ChartingWindowEstimate estimate = estimate(job);
        LocalDateTime now = OffsetDateTime.now(clock).toLocalDateTime();

        if (now.isAfter(estimate.stopPollingAfter())) {
            return new PollingDecision(properties.polling().departureGrace(), "watch-expired");
        }
        if (isQuietHours(job.quietHoursStart(), job.quietHoursEnd(), now.toLocalTime())
                && now.isBefore(estimate.burstPollingStart())) {
            return new PollingDecision(properties.polling().quietHoursInterval(), "quiet-hours");
        }
        if (!now.isBefore(estimate.burstPollingStart())) {
            return new PollingDecision(properties.polling().burstInterval(), "burst-window");
        }
        if (!now.isBefore(estimate.elevatedPollingStart())) {
            return new PollingDecision(properties.polling().elevatedInterval(), "chart-near");
        }
        return new PollingDecision(properties.polling().farInterval(), "baseline");
    }

    public Duration errorBackoff(int failures) {
        long multiplier = Math.max(1, 1L << Math.max(0, failures - 1));
        Duration candidate = properties.polling().initialErrorBackoff().multipliedBy(multiplier);
        return candidate.compareTo(properties.polling().maxErrorBackoff()) > 0
                ? properties.polling().maxErrorBackoff()
                : candidate;
    }

    private boolean isQuietHours(LocalTime start, LocalTime end, LocalTime now) {
        if (start == null || end == null) {
            return false;
        }
        if (start.equals(end)) {
            return true;
        }
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }
        return !now.isBefore(start) || now.isBefore(end);
    }
}
