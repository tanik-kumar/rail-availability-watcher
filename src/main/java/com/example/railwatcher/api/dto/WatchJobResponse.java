package com.example.railwatcher.api.dto;

import com.example.railwatcher.common.model.WatchJob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WatchJobResponse(
        UUID id,
        UUID userId,
        String providerType,
        String trainNumber,
        LocalDate journeyDate,
        String sourceStation,
        String destinationStation,
        String boardingStation,
        String quota,
        String travelClass,
        LocalDateTime originDepartureTime,
        LocalDateTime boardingDepartureTime,
        LocalTime quietHoursStart,
        LocalTime quietHoursEnd,
        String status,
        OffsetDateTime nextPollAt,
        OffsetDateTime lastCheckedAt,
        OffsetDateTime lastAlertAt,
        String currentStatus,
        String currentRawStatus,
        boolean bookingOpen,
        boolean chartPrepared,
        Integer availableSeats,
        boolean notifyTelegram,
        boolean notifyEmail,
        boolean notifyWebhook,
        String note,
        int consecutiveFailures,
        String circuitState,
        OffsetDateTime circuitOpenUntil
) {
    public static WatchJobResponse from(WatchJob job) {
        return new WatchJobResponse(
                job.id(),
                job.userId(),
                job.providerType().name(),
                job.trainNumber(),
                job.journeyDate(),
                job.sourceStation(),
                job.destinationStation(),
                job.boardingStation(),
                job.quota(),
                job.travelClass(),
                job.originDepartureTime(),
                job.boardingDepartureTime(),
                job.quietHoursStart(),
                job.quietHoursEnd(),
                job.status().name(),
                job.nextPollAt(),
                job.lastCheckedAt(),
                job.lastAlertAt(),
                job.currentStatus().name(),
                job.currentRawStatus(),
                job.bookingOpen(),
                job.chartPrepared(),
                job.availableSeats(),
                job.notifyTelegram(),
                job.notifyEmail(),
                job.notifyWebhook(),
                job.note(),
                job.consecutiveFailures(),
                job.circuitState().name(),
                job.circuitOpenUntil()
        );
    }
}
