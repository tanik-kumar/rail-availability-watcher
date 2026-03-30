package com.example.railwatcher.api.dto;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.WatchRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record CreateWatchJobRequest(
        UUID userId,
        @NotNull ProviderType providerType,
        @NotBlank String trainNumber,
        @NotNull LocalDate journeyDate,
        @NotBlank String sourceStation,
        @NotBlank String destinationStation,
        @NotBlank String boardingStation,
        @NotBlank String quota,
        @NotBlank String travelClass,
        @NotNull @FutureOrPresent LocalDateTime originDepartureTime,
        @Future LocalDateTime boardingDepartureTime,
        LocalTime quietHoursStart,
        LocalTime quietHoursEnd,
        boolean notifyTelegram,
        boolean notifyEmail,
        boolean notifyWebhook,
        String note
) {

    public WatchRequest toDomain() {
        return new WatchRequest(
                userId,
                providerType,
                trainNumber,
                journeyDate,
                sourceStation,
                destinationStation,
                boardingStation,
                quota,
                travelClass,
                originDepartureTime,
                boardingDepartureTime,
                quietHoursStart,
                quietHoursEnd,
                notifyTelegram,
                notifyEmail,
                notifyWebhook,
                note
        );
    }
}
