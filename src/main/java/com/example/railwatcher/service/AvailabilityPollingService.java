package com.example.railwatcher.service;

import com.example.railwatcher.common.exception.ProviderException;
import com.example.railwatcher.common.model.AlertCandidate;
import com.example.railwatcher.common.model.CircuitState;
import com.example.railwatcher.common.model.PollingDecision;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.notification.NotificationService;
import com.example.railwatcher.persistence.entity.ProviderErrorEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import com.example.railwatcher.persistence.repository.ProviderErrorRepository;
import com.example.railwatcher.persistence.repository.WatchJobRepository;
import com.example.railwatcher.provider.AvailabilityProviderRegistry;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvailabilityPollingService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityPollingService.class);

    private final WatchJobRepository watchJobRepository;
    private final ProviderErrorRepository providerErrorRepository;
    private final AvailabilityProviderRegistry providerRegistry;
    private final StatusHistoryService statusHistoryService;
    private final AlertDecisionEngine alertDecisionEngine;
    private final NotificationService notificationService;
    private final ChartingWindowEstimator chartingWindowEstimator;
    private final ProviderRateLimiter providerRateLimiter;
    private final Clock clock;
    private final RailwayWatcherProperties properties;
    private final ConcurrentHashMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();

    public AvailabilityPollingService(
            WatchJobRepository watchJobRepository,
            ProviderErrorRepository providerErrorRepository,
            AvailabilityProviderRegistry providerRegistry,
            StatusHistoryService statusHistoryService,
            AlertDecisionEngine alertDecisionEngine,
            NotificationService notificationService,
            ChartingWindowEstimator chartingWindowEstimator,
            ProviderRateLimiter providerRateLimiter,
            Clock clock,
            RailwayWatcherProperties properties
    ) {
        this.watchJobRepository = watchJobRepository;
        this.providerErrorRepository = providerErrorRepository;
        this.providerRegistry = providerRegistry;
        this.statusHistoryService = statusHistoryService;
        this.alertDecisionEngine = alertDecisionEngine;
        this.notificationService = notificationService;
        this.chartingWindowEstimator = chartingWindowEstimator;
        this.providerRateLimiter = providerRateLimiter;
        this.clock = clock;
        this.properties = properties;
    }

    @Transactional
    public void pollDueJob(UUID watchJobId) {
        poll(watchJobId, false);
    }

    @Transactional
    public void pollNow(UUID watchJobId) {
        poll(watchJobId, true);
    }

    private void poll(UUID watchJobId, boolean manualTrigger) {
        ReentrantLock lock = locks.computeIfAbsent(watchJobId, key -> new ReentrantLock());
        if (!lock.tryLock()) {
            return;
        }
        try {
            doPoll(watchJobId, manualTrigger);
        } finally {
            lock.unlock();
        }
    }

    protected void doPoll(UUID watchJobId, boolean manualTrigger) {
        WatchJobEntity job = watchJobRepository.findById(watchJobId).orElse(null);
        if (job == null || job.getStatus() != com.example.railwatcher.common.model.WatchJobStatus.ACTIVE) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        if (!manualTrigger && job.getNextPollAt().isAfter(now)) {
            return;
        }

        if (now.toLocalDateTime().isAfter(chartingWindowEstimator.estimate(job).stopPollingAfter())) {
            expire(job);
            return;
        }

        if (job.getCircuitState() == CircuitState.OPEN && job.getCircuitOpenUntil() != null) {
            if (job.getCircuitOpenUntil().isAfter(now)) {
                job.setNextPollAt(job.getCircuitOpenUntil());
                watchJobRepository.save(job);
                return;
            }
            job.setCircuitState(CircuitState.HALF_OPEN);
        }

        var rateLimited = providerRateLimiter.reserve(job.getProviderType());
        if (rateLimited.isPresent()) {
            job.setNextPollAt(now.plus(rateLimited.get()));
            watchJobRepository.save(job);
            log.info("provider_rate_limited watchJobId={} provider={} delayMs={}",
                    job.getId(), job.getProviderType(), rateLimited.get().toMillis());
            return;
        }

        var previousStatus = job.getCurrentStatus();
        var previousBookingOpen = job.isBookingOpen();
        var previousChartPrepared = job.isChartPrepared();

        try {
            TrainAvailabilitySnapshot snapshot = fetchWithRetry(job);
            statusHistoryService.recordSnapshot(job, snapshot);
            job.setLastCheckedAt(snapshot.fetchedAt());
            job.setCurrentStatus(snapshot.normalizedStatus());
            job.setCurrentRawStatus(snapshot.rawStatus());
            job.setBookingOpen(snapshot.bookingOpen());
            job.setChartPrepared(snapshot.chartPrepared());
            job.setAvailableSeats(snapshot.availableSeats());
            job.setConsecutiveFailures(0);
            job.setCircuitState(CircuitState.CLOSED);
            job.setCircuitOpenUntil(null);
            PollingDecision nextPollingDecision = chartingWindowEstimator.nextPollingDecision(job);
            job.setNextPollAt(now.plus(nextPollingDecision.nextInterval()));
            List<AlertCandidate> alerts = alertDecisionEngine.evaluate(
                    job,
                    previousStatus,
                    previousBookingOpen,
                    previousChartPrepared,
                    snapshot
            );
            notificationService.dispatch(job, alerts);
            watchJobRepository.save(job);
            log.info("availability_polled watchJobId={} status={} bookingOpen={} nextPollAt={}",
                    job.getId(), snapshot.normalizedStatus(), snapshot.bookingOpen(), job.getNextPollAt());
        } catch (ProviderException ex) {
            handleFailure(job, ex);
        }
    }

    private TrainAvailabilitySnapshot fetchWithRetry(WatchJobEntity job) {
        ProviderException lastException = null;
        for (int attempt = 1; attempt <= properties.polling().retryAttempts(); attempt++) {
            try {
                return providerRegistry.getProvider(job.getProviderType()).fetchAvailability(WatchJobMapper.toDomain(job));
            } catch (ProviderException ex) {
                lastException = ex;
                if (!ex.isRetriable() || attempt == properties.polling().retryAttempts()) {
                    throw ex;
                }
                sleep(properties.polling().retryDelay().multipliedBy(attempt));
            }
        }
        throw lastException == null ? new ProviderException("Unknown provider failure.", true) : lastException;
    }

    private void handleFailure(WatchJobEntity job, ProviderException ex) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        int failures = job.getConsecutiveFailures() + 1;
        job.setConsecutiveFailures(failures);

        ProviderErrorEntity providerError = new ProviderErrorEntity();
        providerError.setId(UUID.randomUUID());
        providerError.setWatchJob(job);
        providerError.setProviderType(job.getProviderType());
        providerError.setOccurredAt(now);
        providerError.setErrorType(ex.getClass().getSimpleName());
        providerError.setMessage(ex.getMessage());
        providerError.setConsecutiveFailures(failures);
        providerError.setRetriable(ex.isRetriable());
        providerErrorRepository.save(providerError);

        if (failures >= properties.provider().circuitFailureThreshold()) {
            job.setCircuitState(CircuitState.OPEN);
            job.setCircuitOpenUntil(now.plus(properties.provider().circuitOpenDuration()));
            job.setNextPollAt(job.getCircuitOpenUntil());
        } else {
            Duration backoff = chartingWindowEstimator.errorBackoff(failures);
            job.setNextPollAt(now.plus(backoff));
        }
        watchJobRepository.save(job);
        log.warn("availability_poll_failed watchJobId={} provider={} retriable={} message={}",
                job.getId(), job.getProviderType(), ex.isRetriable(), ex.getMessage());
    }

    private void expire(WatchJobEntity job) {
        if (job.getStatus() == com.example.railwatcher.common.model.WatchJobStatus.EXPIRED) {
            return;
        }
        job.setStatus(com.example.railwatcher.common.model.WatchJobStatus.EXPIRED);
        job.setNextPollAt(OffsetDateTime.now(clock).plusDays(365));
        notificationService.dispatch(job, List.of(alertDecisionEngine.expired(job)));
        watchJobRepository.save(job);
        log.info("watch_expired watchJobId={} trainNumber={}", job.getId(), job.getTrainNumber());
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new ProviderException("Retry wait interrupted.", true, interruptedException);
        }
    }
}
