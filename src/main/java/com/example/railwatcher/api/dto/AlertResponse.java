package com.example.railwatcher.api.dto;

import com.example.railwatcher.persistence.entity.AlertEntity;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        String alertType,
        String channel,
        String dedupeKey,
        String title,
        String body,
        boolean strongAlert,
        OffsetDateTime sentAt,
        String deliveryStatus,
        String metadata
) {
    public static AlertResponse from(AlertEntity entity) {
        return new AlertResponse(
                entity.getId(),
                entity.getAlertType().name(),
                entity.getChannel().name(),
                entity.getDedupeKey(),
                entity.getTitle(),
                entity.getBody(),
                entity.isStrongAlert(),
                entity.getSentAt(),
                entity.getDeliveryStatus(),
                entity.getMetadata()
        );
    }
}
