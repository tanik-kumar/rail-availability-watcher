package com.example.railwatcher.api.dto;

import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.persistence.entity.TrainStatusHistoryEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StatusHistoryResponse(
        UUID id,
        OffsetDateTime polledAt,
        String providerType,
        String normalizedStatus,
        String rawStatus,
        boolean bookingOpen,
        boolean chartPrepared,
        Integer availableSeats,
        Long responseTimeMs,
        String providerReference,
        String note
) {
    public static StatusHistoryResponse from(TrainStatusHistoryEntity entity) {
        return new StatusHistoryResponse(
                entity.getId(),
                entity.getPolledAt(),
                entity.getProviderType().name(),
                entity.getNormalizedStatus().name(),
                entity.getRawStatus(),
                entity.isBookingOpen(),
                entity.isChartPrepared(),
                entity.getAvailableSeats(),
                entity.getResponseTimeMs(),
                entity.getProviderReference(),
                entity.getNote()
        );
    }

    public static StatusHistoryResponse from(WatchJob job) {
        return new StatusHistoryResponse(
                job.id(),
                job.lastCheckedAt(),
                job.providerType().name(),
                job.currentStatus().name(),
                job.currentRawStatus(),
                job.bookingOpen(),
                job.chartPrepared(),
                job.availableSeats(),
                null,
                null,
                job.note()
        );
    }
}
