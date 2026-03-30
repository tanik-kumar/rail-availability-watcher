package com.example.railwatcher.common.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WatchJob(
        UUID id,
        UUID userId,
        ProviderType providerType,
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
        WatchJobStatus status,
        OffsetDateTime nextPollAt,
        OffsetDateTime lastCheckedAt,
        OffsetDateTime lastAlertAt,
        NormalizedAvailabilityStatus currentStatus,
        String currentRawStatus,
        boolean bookingOpen,
        boolean chartPrepared,
        Integer availableSeats,
        boolean notifyTelegram,
        boolean notifyEmail,
        boolean notifyWebhook,
        String note,
        int consecutiveFailures,
        CircuitState circuitState,
        OffsetDateTime circuitOpenUntil
) {
}
