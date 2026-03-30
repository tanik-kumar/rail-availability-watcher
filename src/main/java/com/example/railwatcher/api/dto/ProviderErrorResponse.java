package com.example.railwatcher.api.dto;

import com.example.railwatcher.persistence.entity.ProviderErrorEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProviderErrorResponse(
        UUID id,
        String providerType,
        OffsetDateTime occurredAt,
        String errorType,
        String message,
        int consecutiveFailures,
        Integer httpStatus,
        boolean retriable
) {
    public static ProviderErrorResponse from(ProviderErrorEntity entity) {
        return new ProviderErrorResponse(
                entity.getId(),
                entity.getProviderType().name(),
                entity.getOccurredAt(),
                entity.getErrorType(),
                entity.getMessage(),
                entity.getConsecutiveFailures(),
                entity.getHttpStatus(),
                entity.isRetriable()
        );
    }
}
