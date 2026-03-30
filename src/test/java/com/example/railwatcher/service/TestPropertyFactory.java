package com.example.railwatcher.service;

import com.example.railwatcher.config.RailwayWatcherProperties;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public final class TestPropertyFactory {

    private TestPropertyFactory() {
    }

    public static RailwayWatcherProperties properties() {
        return new RailwayWatcherProperties(
                new RailwayWatcherProperties.SchedulerProperties(Duration.ofSeconds(15), 20),
                new RailwayWatcherProperties.PollingProperties(
                        Duration.ofMinutes(20),
                        Duration.ofMinutes(5),
                        Duration.ofSeconds(45),
                        Duration.ofHours(1),
                        Duration.ofMinutes(30),
                        Duration.ofMinutes(1),
                        Duration.ofMinutes(15),
                        3,
                        Duration.ofSeconds(2),
                        Duration.ofMinutes(20)
                ),
                new RailwayWatcherProperties.ChartingProperties(
                        Duration.ofHours(4),
                        Duration.ofMinutes(90),
                        Duration.ofHours(2),
                        Duration.ofMinutes(30)
                ),
                new RailwayWatcherProperties.ProviderProperties(
                        Duration.ofSeconds(5),
                        Duration.ofSeconds(10),
                        10,
                        5,
                        Duration.ofMinutes(15),
                        "",
                        "/api/availability",
                        "TestAgent"
                ),
                new RailwayWatcherProperties.NotificationProperties(
                        "test@example.com",
                        "",
                        "https://api.telegram.org",
                        Duration.ofMinutes(5)
                ),
                new RailwayWatcherProperties.RedisProperties(false, "test", Duration.ofHours(6)),
                new RailwayWatcherProperties.AliasProperties(Map.of(), Map.of()),
                new RailwayWatcherProperties.AdminUserProperties(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "Test Admin",
                        "test@example.com",
                        "",
                        "",
                        "Asia/Kolkata"
                )
        );
    }
}
