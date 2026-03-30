package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.AlertCandidate;
import com.example.railwatcher.persistence.entity.AlertEntity;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import com.example.railwatcher.persistence.repository.AlertRepository;
import com.example.railwatcher.config.RailwayWatcherProperties;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompositeNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(CompositeNotificationService.class);

    private final List<NotificationChannelSender> senders;
    private final AlertRepository alertRepository;
    private final AlertDedupeStore dedupeStore;
    private final RailwayWatcherProperties properties;
    private final Clock clock;

    public CompositeNotificationService(
            List<NotificationChannelSender> senders,
            AlertRepository alertRepository,
            AlertDedupeStore dedupeStore,
            RailwayWatcherProperties properties,
            Clock clock
    ) {
        this.senders = senders;
        this.alertRepository = alertRepository;
        this.dedupeStore = dedupeStore;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void dispatch(WatchJobEntity watchJob, List<AlertCandidate> alerts) {
        if (alerts.isEmpty()) {
            return;
        }
        UserEntity user = watchJob.getUser();
        OffsetDateTime now = OffsetDateTime.now(clock);
        for (AlertCandidate alert : alerts) {
            var dedupeTtl = alert.strongAlert()
                    ? properties.notification().strongAlertRepeatCooldown()
                    : properties.polling().alertCooldown();
            if (alertRepository.existsByWatchJobIdAndDedupeKeyAndSentAtAfter(
                    watchJob.getId(),
                    alert.dedupeKey(),
                    now.minus(properties.polling().alertCooldown()))
                    || !dedupeStore.markIfNew(watchJob.getId(), alert.dedupeKey(), dedupeTtl)) {
                continue;
            }
            NotificationMessage message = new NotificationMessage(alert.title(), alert.body(), alert.strongAlert());
            for (NotificationChannelSender sender : senders) {
                if (!sender.isEnabled(user, watchJob)) {
                    continue;
                }
                String deliveryStatus = "SENT";
                String metadata = null;
                try {
                    sender.send(message, user, watchJob);
                } catch (Exception ex) {
                    deliveryStatus = "FAILED";
                    metadata = ex.getMessage();
                    log.warn("notification_delivery_failed watchJobId={} channel={} message={}",
                            watchJob.getId(), sender.channel(), ex.getMessage());
                }
                AlertEntity entity = new AlertEntity();
                entity.setId(UUID.randomUUID());
                entity.setWatchJob(watchJob);
                entity.setUser(user);
                entity.setAlertType(alert.alertType());
                entity.setChannel(sender.channel());
                entity.setDedupeKey(alert.dedupeKey());
                entity.setTitle(alert.title());
                entity.setBody(alert.body());
                entity.setStrongAlert(alert.strongAlert());
                entity.setSentAt(now);
                entity.setDeliveryStatus(deliveryStatus);
                entity.setMetadata(metadata);
                alertRepository.save(entity);
            }
            watchJob.setLastAlertAt(now);
        }
    }
}
