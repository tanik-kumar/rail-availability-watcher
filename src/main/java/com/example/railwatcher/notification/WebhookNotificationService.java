package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.NotificationChannel;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebhookNotificationService implements NotificationChannelSender {

    private final WebClient webClient;

    public WebhookNotificationService(WebClient railwayWebClient) {
        this.webClient = railwayWebClient;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.WEBHOOK;
    }

    @Override
    public boolean isEnabled(UserEntity user, WatchJobEntity job) {
        return job.isNotifyWebhook() && StringUtils.hasText(user.getWebhookUrl());
    }

    @Override
    public void send(NotificationMessage message, UserEntity user, WatchJobEntity job) {
        webClient.post()
                .uri(user.getWebhookUrl())
                .bodyValue(Map.of(
                        "watchJobId", job.getId(),
                        "title", message.title(),
                        "body", message.body(),
                        "strongAlert", message.strongAlert()
                ))
                .retrieve()
                .toBodilessEntity()
                .timeout(java.time.Duration.ofSeconds(10))
                .block();
    }
}
