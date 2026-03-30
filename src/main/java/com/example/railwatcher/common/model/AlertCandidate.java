package com.example.railwatcher.common.model;

public record AlertCandidate(
        AlertType alertType,
        String dedupeKey,
        String title,
        String body,
        boolean strongAlert
) {
}
