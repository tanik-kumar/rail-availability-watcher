package com.example.railwatcher.service;

import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.persistence.entity.WatchJobEntity;

public final class WatchJobMapper {

    private WatchJobMapper() {
    }

    public static WatchJob toDomain(WatchJobEntity entity) {
        return new WatchJob(
                entity.getId(),
                entity.getUser().getId(),
                entity.getProviderType(),
                entity.getTrainNumber(),
                entity.getJourneyDate(),
                entity.getSourceStation(),
                entity.getDestinationStation(),
                entity.getBoardingStation(),
                entity.getQuota(),
                entity.getTravelClass(),
                entity.getOriginDepartureTime(),
                entity.getBoardingDepartureTime(),
                entity.getQuietHoursStart(),
                entity.getQuietHoursEnd(),
                entity.getStatus(),
                entity.getNextPollAt(),
                entity.getLastCheckedAt(),
                entity.getLastAlertAt(),
                entity.getCurrentStatus(),
                entity.getCurrentRawStatus(),
                entity.isBookingOpen(),
                entity.isChartPrepared(),
                entity.getAvailableSeats(),
                entity.isNotifyTelegram(),
                entity.isNotifyEmail(),
                entity.isNotifyWebhook(),
                entity.getNote(),
                entity.getConsecutiveFailures(),
                entity.getCircuitState(),
                entity.getCircuitOpenUntil()
        );
    }
}
