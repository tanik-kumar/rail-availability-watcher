package com.example.railwatcher.common.model;

import java.time.OffsetDateTime;

public record TrainAvailabilitySnapshot(
        ProviderType providerType,
        OffsetDateTime fetchedAt,
        NormalizedAvailabilityStatus normalizedStatus,
        String rawStatus,
        boolean bookingOpen,
        boolean chartPrepared,
        Integer availableSeats,
        Long responseTimeMs,
        String providerReference,
        String note
) {
}
