package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.NotificationChannel;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;

public interface NotificationChannelSender {

    NotificationChannel channel();

    boolean isEnabled(UserEntity user, WatchJobEntity job);

    void send(NotificationMessage message, UserEntity user, WatchJobEntity job);
}
