package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.NotificationChannel;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TelegramNotificationService implements NotificationChannelSender {

    private final WebClient webClient;
    private final RailwayWatcherProperties properties;

    public TelegramNotificationService(WebClient railwayWebClient, RailwayWatcherProperties properties) {
        this.webClient = railwayWebClient;
        this.properties = properties;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.TELEGRAM;
    }

    @Override
    public boolean isEnabled(UserEntity user, WatchJobEntity job) {
        return job.isNotifyTelegram()
                && StringUtils.hasText(properties.notification().telegramBotToken())
                && StringUtils.hasText(user.getTelegramChatId());
    }

    @Override
    public void send(NotificationMessage message, UserEntity user, WatchJobEntity job) {
        webClient.post()
                .uri(properties.notification().telegramApiBaseUrl()
                        + "/bot" + properties.notification().telegramBotToken() + "/sendMessage")
                .bodyValue(Map.of(
                        "chat_id", user.getTelegramChatId(),
                        "text", message.formattedText(),
                        "disable_web_page_preview", true
                ))
                .retrieve()
                .toBodilessEntity()
                .timeout(java.time.Duration.ofSeconds(10))
                .block();
    }
}
