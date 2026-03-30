package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.NotificationChannel;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmailNotificationService implements NotificationChannelSender {

    private final JavaMailSender mailSender;
    private final RailwayWatcherProperties properties;

    public EmailNotificationService(JavaMailSender mailSender, RailwayWatcherProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean isEnabled(UserEntity user, WatchJobEntity job) {
        return job.isNotifyEmail()
                && StringUtils.hasText(properties.notification().emailFrom())
                && StringUtils.hasText(user.getEmail());
    }

    @Override
    public void send(NotificationMessage message, UserEntity user, WatchJobEntity job) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(properties.notification().emailFrom());
        mail.setTo(user.getEmail());
        mail.setSubject(message.title());
        mail.setText(message.body());
        mailSender.send(mail);
    }
}
