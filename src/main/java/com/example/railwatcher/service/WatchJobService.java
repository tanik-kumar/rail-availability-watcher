package com.example.railwatcher.service;

import com.example.railwatcher.common.exception.NotFoundException;
import com.example.railwatcher.common.model.CircuitState;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.common.model.WatchJobStatus;
import com.example.railwatcher.common.model.WatchRequest;
import com.example.railwatcher.common.util.AliasResolver;
import com.example.railwatcher.persistence.entity.AlertEntity;
import com.example.railwatcher.persistence.entity.ProviderErrorEntity;
import com.example.railwatcher.persistence.entity.TrainStatusHistoryEntity;
import com.example.railwatcher.persistence.entity.UserEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import com.example.railwatcher.persistence.repository.AlertRepository;
import com.example.railwatcher.persistence.repository.ProviderErrorRepository;
import com.example.railwatcher.persistence.repository.TrainStatusHistoryRepository;
import com.example.railwatcher.persistence.repository.UserRepository;
import com.example.railwatcher.persistence.repository.WatchJobRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WatchJobService {

    private final WatchJobRepository watchJobRepository;
    private final UserRepository userRepository;
    private final TrainStatusHistoryRepository historyRepository;
    private final AlertRepository alertRepository;
    private final ProviderErrorRepository providerErrorRepository;
    private final AliasResolver aliasResolver;
    private final Clock clock;
    private final ChartingWindowEstimator chartingWindowEstimator;

    public WatchJobService(
            WatchJobRepository watchJobRepository,
            UserRepository userRepository,
            TrainStatusHistoryRepository historyRepository,
            AlertRepository alertRepository,
            ProviderErrorRepository providerErrorRepository,
            AliasResolver aliasResolver,
            Clock clock,
            ChartingWindowEstimator chartingWindowEstimator
    ) {
        this.watchJobRepository = watchJobRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.alertRepository = alertRepository;
        this.providerErrorRepository = providerErrorRepository;
        this.aliasResolver = aliasResolver;
        this.clock = clock;
        this.chartingWindowEstimator = chartingWindowEstimator;
    }

    @Transactional
    public WatchJob create(WatchRequest request, UUID defaultUserId) {
        UserEntity user = resolveUser(request.userId(), defaultUserId);
        WatchJobEntity entity = new WatchJobEntity();
        entity.setId(UUID.randomUUID());
        entity.setUser(user);
        applyRequest(entity, request);
        entity.setStatus(WatchJobStatus.ACTIVE);
        entity.setNextPollAt(OffsetDateTime.now(clock));
        entity.setCurrentStatus(NormalizedAvailabilityStatus.UNKNOWN);
        entity.setCurrentRawStatus("NOT_CHECKED");
        entity.setBookingOpen(false);
        entity.setChartPrepared(false);
        entity.setConsecutiveFailures(0);
        entity.setCircuitState(CircuitState.CLOSED);
        entity.setCircuitOpenUntil(null);
        return WatchJobMapper.toDomain(watchJobRepository.save(entity));
    }

    @Transactional
    public WatchJob update(UUID id, WatchRequest request, UUID defaultUserId) {
        WatchJobEntity entity = requireEntity(id);
        entity.setUser(resolveUser(request.userId(), defaultUserId));
        applyRequest(entity, request);
        entity.setNextPollAt(OffsetDateTime.now(clock));
        return WatchJobMapper.toDomain(watchJobRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<WatchJob> list() {
        return watchJobRepository.findAllByOrderByCreatedAtDesc().stream().map(WatchJobMapper::toDomain).toList();
    }

    @Transactional(readOnly = true)
    public WatchJob get(UUID id) {
        return WatchJobMapper.toDomain(requireEntity(id));
    }

    @Transactional
    public WatchJob pause(UUID id) {
        WatchJobEntity entity = requireEntity(id);
        entity.setStatus(WatchJobStatus.PAUSED);
        return WatchJobMapper.toDomain(watchJobRepository.save(entity));
    }

    @Transactional
    public WatchJob resume(UUID id) {
        WatchJobEntity entity = requireEntity(id);
        entity.setStatus(WatchJobStatus.ACTIVE);
        entity.setNextPollAt(OffsetDateTime.now(clock));
        return WatchJobMapper.toDomain(watchJobRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        WatchJobEntity entity = requireEntity(id);
        watchJobRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<TrainStatusHistoryEntity> history(UUID id) {
        requireEntity(id);
        return historyRepository.findTop100ByWatchJobIdOrderByPolledAtDesc(id);
    }

    @Transactional(readOnly = true)
    public List<AlertEntity> alerts(UUID id) {
        requireEntity(id);
        return alertRepository.findTop50ByWatchJobIdOrderBySentAtDesc(id);
    }

    @Transactional(readOnly = true)
    public List<ProviderErrorEntity> providerErrors(UUID id) {
        requireEntity(id);
        return providerErrorRepository.findTop50ByWatchJobIdOrderByOccurredAtDesc(id);
    }

    @Transactional(readOnly = true)
    public WatchJobEntity requireEntity(UUID id) {
        return watchJobRepository.findById(id).orElseThrow(() -> new NotFoundException("Watcher not found: " + id));
    }

    private void applyRequest(WatchJobEntity entity, WatchRequest request) {
        entity.setProviderType(request.providerType());
        entity.setTrainNumber(aliasResolver.resolveTrain(request.trainNumber()));
        entity.setJourneyDate(request.journeyDate());
        entity.setSourceStation(aliasResolver.resolveStation(request.sourceStation()));
        entity.setDestinationStation(aliasResolver.resolveStation(request.destinationStation()));
        entity.setBoardingStation(aliasResolver.resolveStation(request.boardingStation()));
        entity.setQuota(request.quota().trim().toUpperCase());
        entity.setTravelClass(request.travelClass().trim().toUpperCase());
        entity.setOriginDepartureTime(request.originDepartureTime());
        entity.setBoardingDepartureTime(request.boardingDepartureTime());
        entity.setQuietHoursStart(request.quietHoursStart());
        entity.setQuietHoursEnd(request.quietHoursEnd());
        entity.setNotifyTelegram(request.notifyTelegram());
        entity.setNotifyEmail(request.notifyEmail());
        entity.setNotifyWebhook(request.notifyWebhook());
        entity.setNote(request.note());
        chartingWindowEstimator.estimate(entity);
    }

    private UserEntity resolveUser(UUID requestedUserId, UUID defaultUserId) {
        UUID userId = requestedUserId != null ? requestedUserId : defaultUserId;
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}
