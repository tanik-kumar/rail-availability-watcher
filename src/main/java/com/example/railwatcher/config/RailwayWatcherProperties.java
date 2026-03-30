package com.example.railwatcher.config;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "railway")
public record RailwayWatcherProperties(
        SchedulerProperties scheduler,
        PollingProperties polling,
        ChartingProperties charting,
        ProviderProperties provider,
        NotificationProperties notification,
        RedisProperties redis,
        AliasProperties aliases,
        AdminUserProperties adminUser
) {

    public record SchedulerProperties(
            Duration dispatchInterval,
            int batchSize
    ) {
    }

    public record PollingProperties(
            Duration farInterval,
            Duration elevatedInterval,
            Duration burstInterval,
            Duration quietHoursInterval,
            Duration maxErrorBackoff,
            Duration initialErrorBackoff,
            Duration departureGrace,
            int retryAttempts,
            Duration retryDelay,
            Duration alertCooldown
    ) {
    }

    public record ChartingProperties(
            Duration originLeadTime,
            Duration remoteLeadTime,
            Duration elevatedWindow,
            Duration burstWindow
    ) {
    }

    public record ProviderProperties(
            Duration connectTimeout,
            Duration readTimeout,
            int permitsPerMinute,
            int circuitFailureThreshold,
            Duration circuitOpenDuration,
            String httpBaseUrl,
            String httpEndpointPath,
            String userAgent
    ) {
    }

    public record NotificationProperties(
            String emailFrom,
            String telegramBotToken,
            String telegramApiBaseUrl,
            Duration strongAlertRepeatCooldown
    ) {
    }

    public record RedisProperties(
            boolean enabled,
            String keyPrefix,
            Duration ttl
    ) {
    }

    public record AliasProperties(
            Map<String, String> stations,
            Map<String, String> trains
    ) {
        public AliasProperties {
            stations = stations == null ? new LinkedHashMap<>() : stations;
            trains = trains == null ? new LinkedHashMap<>() : trains;
        }
    }

    public record AdminUserProperties(
            UUID id,
            String name,
            String email,
            String telegramChatId,
            String webhookUrl,
            String timezone
    ) {
    }
}
