package com.example.railwatcher.service;

import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.repository.UserRepository;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class UserBootstrapService implements ApplicationRunner {

    private final RailwayWatcherProperties properties;
    private final UserRepository userRepository;

    public UserBootstrapService(RailwayWatcherProperties properties, UserRepository userRepository) {
        this.properties = properties;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        RailwayWatcherProperties.AdminUserProperties adminUser = properties.adminUser();
        if (adminUser == null || adminUser.id() == null || adminUser.email() == null) {
            return;
        }
        userRepository.findById(adminUser.id()).orElseGet(() -> {
            UserEntity user = new UserEntity();
            user.setId(adminUser.id() == null ? UUID.randomUUID() : adminUser.id());
            user.setName(adminUser.name());
            user.setEmail(adminUser.email());
            user.setTelegramChatId(adminUser.telegramChatId());
            user.setWebhookUrl(adminUser.webhookUrl());
            user.setTimezone(adminUser.timezone());
            return userRepository.save(user);
        });
    }
}
