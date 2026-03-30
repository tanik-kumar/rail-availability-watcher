package com.example.railwatcher.scheduler;

import com.example.railwatcher.common.model.WatchJobStatus;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.persistence.repository.WatchJobRepository;
import com.example.railwatcher.service.AvailabilityPollingService;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WatchJobScheduler {

    private final WatchJobRepository watchJobRepository;
    private final AvailabilityPollingService pollingService;
    private final RailwayWatcherProperties properties;
    private final TaskScheduler taskScheduler;
    private final Clock clock;

    public WatchJobScheduler(
            WatchJobRepository watchJobRepository,
            AvailabilityPollingService pollingService,
            RailwayWatcherProperties properties,
            TaskScheduler taskScheduler,
            Clock clock
    ) {
        this.watchJobRepository = watchJobRepository;
        this.pollingService = pollingService;
        this.properties = properties;
        this.taskScheduler = taskScheduler;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${railway.scheduler.dispatch-interval:15s}")
    public void dispatchDueJobs() {
        OffsetDateTime now = OffsetDateTime.now(clock);
        watchJobRepository.findTop50ByStatusAndNextPollAtLessThanEqualOrderByNextPollAtAsc(WatchJobStatus.ACTIVE, now)
                .stream()
                .limit(properties.scheduler().batchSize())
                .forEach(job -> taskScheduler.schedule(
                        () -> pollingService.pollDueJob(job.getId()),
                        java.util.Date.from(now.toInstant())
                ));
    }
}
