package com.example.railwatcher.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.railwatcher.common.model.AlertType;
import com.example.railwatcher.common.model.CircuitState;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.common.model.WatchJobStatus;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AlertDecisionEngineTest {

    private final AlertDecisionEngine engine = new AlertDecisionEngine();

    @Test
    void shouldRaiseStrongAlertsWhenBookingOpensAndSeatBecomesAvailable() {
        WatchJobEntity job = sampleJob();
        TrainAvailabilitySnapshot snapshot = new TrainAvailabilitySnapshot(
                ProviderType.MOCK,
                OffsetDateTime.parse("2026-04-02T02:35:00Z"),
                NormalizedAvailabilityStatus.AVAILABLE,
                "AVAILABLE 3",
                true,
                true,
                3,
                70L,
                "mock://post-chart",
                "Strong transition"
        );

        List<AlertType> types = engine.evaluate(job, NormalizedAvailabilityStatus.WAITLIST, false, false, snapshot)
                .stream()
                .map(alert -> alert.alertType())
                .toList();

        assertThat(types).contains(AlertType.BOOKING_OPEN, AlertType.AVAILABLE, AlertType.CHART_PREPARED);
    }

    @Test
    void shouldReturnNoAlertsWhenStateDoesNotChange() {
        WatchJobEntity job = sampleJob();
        TrainAvailabilitySnapshot snapshot = new TrainAvailabilitySnapshot(
                ProviderType.MOCK,
                OffsetDateTime.parse("2026-04-02T02:35:00Z"),
                NormalizedAvailabilityStatus.UNKNOWN,
                "CHART NOT PREPARED",
                false,
                false,
                null,
                70L,
                "mock://pre-chart",
                "No change"
        );

        assertThat(engine.evaluate(job, NormalizedAvailabilityStatus.UNKNOWN, false, false, snapshot)).isEmpty();
    }

    @Test
    void shouldEmitStatusChangeWhenStatusMovesButIsNotBookable() {
        WatchJobEntity job = sampleJob();
        TrainAvailabilitySnapshot snapshot = new TrainAvailabilitySnapshot(
                ProviderType.MOCK,
                OffsetDateTime.parse("2026-04-02T02:35:00Z"),
                NormalizedAvailabilityStatus.NOT_AVAILABLE,
                "REGRET",
                false,
                true,
                null,
                70L,
                "mock://mid-chart",
                "Change only"
        );

        assertThat(engine.evaluate(job, NormalizedAvailabilityStatus.UNKNOWN, false, false, snapshot))
                .extracting(alert -> alert.alertType())
                .contains(AlertType.CHART_PREPARED, AlertType.STATUS_CHANGE);
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
        job.setJourneyDate(java.time.LocalDate.parse("2026-04-02"));
        job.setSourceStation("NDLS");
        job.setDestinationStation("LKO");
        job.setBoardingStation("CNB");
        job.setQuota("GN");
        job.setTravelClass("3A");
        job.setOriginDepartureTime(java.time.LocalDateTime.parse("2026-04-02T06:10:00"));
        job.setBoardingDepartureTime(java.time.LocalDateTime.parse("2026-04-02T10:25:00"));
        job.setStatus(WatchJobStatus.ACTIVE);
        job.setNextPollAt(OffsetDateTime.parse("2026-04-01T20:00:00Z"));
        job.setCurrentStatus(NormalizedAvailabilityStatus.UNKNOWN);
        job.setCurrentRawStatus("UNKNOWN");
        job.setCircuitState(CircuitState.CLOSED);
        return job;
    }
}
