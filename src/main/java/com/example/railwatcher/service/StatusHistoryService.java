package com.example.railwatcher.service;

import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.persistence.entity.TrainStatusHistoryEntity;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import com.example.railwatcher.persistence.repository.TrainStatusHistoryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatusHistoryService {

    private final TrainStatusHistoryRepository historyRepository;

    public StatusHistoryService(TrainStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public TrainStatusHistoryEntity recordSnapshot(WatchJobEntity job, TrainAvailabilitySnapshot snapshot) {
        TrainStatusHistoryEntity entity = new TrainStatusHistoryEntity();
        entity.setId(UUID.randomUUID());
        entity.setWatchJob(job);
        entity.setProviderType(snapshot.providerType());
        entity.setPolledAt(snapshot.fetchedAt());
        entity.setNormalizedStatus(snapshot.normalizedStatus());
        entity.setRawStatus(snapshot.rawStatus());
        entity.setBookingOpen(snapshot.bookingOpen());
        entity.setChartPrepared(snapshot.chartPrepared());
        entity.setAvailableSeats(snapshot.availableSeats());
        entity.setResponseTimeMs(snapshot.responseTimeMs());
        entity.setProviderReference(snapshot.providerReference());
        entity.setNote(snapshot.note());
        return historyRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<TrainStatusHistoryEntity> historyForJob(UUID jobId) {
        return historyRepository.findTop100ByWatchJobIdOrderByPolledAtDesc(jobId);
    }

    @Transactional(readOnly = true)
    public TrainStatusHistoryEntity latestForJob(UUID jobId) {
        return historyRepository.findTopByWatchJobIdOrderByPolledAtDesc(jobId);
    }
}
