package com.example.railwatcher.provider;

import com.example.railwatcher.common.model.ChartingWindowEstimate;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.service.ChartingWindowEstimator;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MockAvailabilityProvider implements AvailabilityProvider {

    private final Clock clock;
    private final ChartingWindowEstimator chartingWindowEstimator;

    public MockAvailabilityProvider(Clock clock, ChartingWindowEstimator chartingWindowEstimator) {
        this.clock = clock;
        this.chartingWindowEstimator = chartingWindowEstimator;
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.MOCK;
    }

    @Override
    public TrainAvailabilitySnapshot fetchAvailability(WatchJob job) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        ChartingWindowEstimate estimate = chartingWindowEstimator.estimate(job);
        LocalDateTime nowLocal = now.toLocalDateTime();
        int seed = Math.abs(Objects.hash(job.id(), now.getHour(), now.getMinute() / 5));

        if (nowLocal.isAfter(estimate.stopPollingAfter())) {
            return new TrainAvailabilitySnapshot(
                    ProviderType.MOCK,
                    now,
                    NormalizedAvailabilityStatus.DEPARTED,
                    "DEPARTED",
                    false,
                    true,
                    null,
                    80L,
                    "mock://departed",
                    "Mock provider marks the watcher as departed after the configured grace window."
            );
        }

        if (nowLocal.isBefore(estimate.originChartTime())) {
            boolean waitlist = seed % 2 == 0;
            return new TrainAvailabilitySnapshot(
                    ProviderType.MOCK,
                    now,
                    waitlist ? NormalizedAvailabilityStatus.WAITLIST : NormalizedAvailabilityStatus.UNKNOWN,
                    waitlist ? "WL 12" : "CHART NOT PREPARED",
                    false,
                    false,
                    null,
                    110L,
                    "mock://pre-chart",
                    "Current booking is not open yet. The mock provider keeps the watcher in a pre-chart state."
            );
        }

        if (estimate.remoteBoarding()
                && estimate.remoteChartTime() != null
                && nowLocal.isBefore(estimate.remoteChartTime())) {
            return new TrainAvailabilitySnapshot(
                    ProviderType.MOCK,
                    now,
                    NormalizedAvailabilityStatus.NOT_AVAILABLE,
                    "REMOTE CHART PENDING",
                    false,
                    true,
                    null,
                    95L,
                    "mock://remote-pending",
                    "Origin chart is treated as prepared, but remote-location current booking stays closed until the secondary chart window."
            );
        }

        int seats = Math.max(1, seed % 9);
        boolean immediatelyAvailable = seed % 4 != 0;
        return new TrainAvailabilitySnapshot(
                ProviderType.MOCK,
                now,
                immediatelyAvailable ? NormalizedAvailabilityStatus.AVAILABLE : NormalizedAvailabilityStatus.BOOKABLE,
                immediatelyAvailable ? "AVAILABLE " + seats : "BOOKING OPEN",
                true,
                true,
                seats,
                70L,
                "mock://post-chart",
                "Mock provider simulates bookable current availability after chart preparation."
        );
    }
}
