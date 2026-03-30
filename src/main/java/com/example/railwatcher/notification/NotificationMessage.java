package com.example.railwatcher.notification;

public record NotificationMessage(
        String title,
        String body,
        boolean strongAlert
) {
    public String formattedText() {
        return title + "\n" + body;
    }
}
