package com.example.railwatcher.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.railwatcher.common.model.CircuitState;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.WatchJobStatus;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChartingWindowEstimatorTest {

    @Test
    void shouldEstimateOriginAndRemoteChartWindows() {
        ChartingWindowEstimator estimator = new ChartingWindowEstimator(
                Clock.fixed(Instant.parse("2026-04-02T00:00:00Z"), ZoneId.of("UTC")),
                TestPropertyFactory.properties()
        );
        WatchJobEntity job = sampleJob();

        var estimate = estimator.estimate(job);

        assertThat(estimate.originChartTime()).isEqualTo(LocalDateTime.parse("2026-04-02T02:10:00"));
        assertThat(estimate.remoteChartTime()).isEqualTo(LocalDateTime.parse("2026-04-02T08:55:00"));
        assertThat(estimate.activeChartTime()).isEqualTo(LocalDateTime.parse("2026-04-02T02:10:00"));
    }

    @Test
    void shouldUseBurstIntervalNearChartWindow() {
        ChartingWindowEstimator estimator = new ChartingWindowEstimator(
                Clock.fixed(Instant.parse("2026-04-02T01:50:00Z"), ZoneId.of("UTC")),
                TestPropertyFactory.properties()
        );

        assertThat(estimator.nextPollingDecision(sampleJob()).reason()).isEqualTo("burst-window");
        assertThat(estimator.nextPollingDecision(sampleJob()).nextInterval()).hasSeconds(45);
    }

    @Test
    void shouldUseQuietHoursIntervalWhenConfiguredAwayFromBurstWindow() {
        ChartingWindowEstimator estimator = new ChartingWindowEstimator(
                Clock.fixed(Instant.parse("2026-04-01T20:00:00Z"), ZoneId.of("UTC")),
                TestPropertyFactory.properties()
        );
        WatchJobEntity job = sampleJob();
        job.setQuietHoursStart(java.time.LocalTime.parse("19:30:00"));
        job.setQuietHoursEnd(java.time.LocalTime.parse("23:30:00"));

        assertThat(estimator.nextPollingDecision(job).reason()).isEqualTo("quiet-hours");
    }

    private WatchJobEntity sampleJob() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setName("Test");
        user.setEmail("test@example.com");
        user.setTimezone("Asia/Kolkata");

        WatchJobEntity job = new WatchJobEntity();
        job.setId(UUID.randomUUID());
        job.setUser(user);
        job.setProviderType(ProviderType.MOCK);
        job.setTrainNumber("12004");
        job.setJourneyDate(LocalDate.parse("2026-04-02"));
        job.setSourceStation("NDLS");
        job.setDestinationStation("LKO");
        job.setBoardingStation("CNB");
        job.setQuota("GN");
        job.setTravelClass("3A");
        job.setOriginDepartureTime(LocalDateTime.parse("2026-04-02T06:10:00"));
        job.setBoardingDepartureTime(LocalDateTime.parse("2026-04-02T10:25:00"));
        job.setStatus(WatchJobStatus.ACTIVE);
        job.setNextPollAt(OffsetDateTime.parse("2026-04-01T20:00:00Z"));
        job.setCurrentStatus(NormalizedAvailabilityStatus.UNKNOWN);
        job.setCurrentRawStatus("UNKNOWN");
        job.setCircuitState(CircuitState.CLOSED);
        return job;
    }
}
