package com.example.railwatcher.service;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import com.example.railwatcher.persistence.repository.WatchJobRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("providerHealthIndicator")
public class ProviderHealthService implements HealthIndicator {

    private final WatchJobRepository watchJobRepository;

    public ProviderHealthService(WatchJobRepository watchJobRepository) {
        this.watchJobRepository = watchJobRepository;
    }

    public Map<String, Object> snapshot() {
        List<WatchJobEntity> jobs = watchJobRepository.findAllByOrderByCreatedAtDesc();
        Map<String, Object> details = new LinkedHashMap<>();
        for (ProviderType providerType : ProviderType.values()) {
            long openCircuits = jobs.stream()
                    .filter(job -> job.getProviderType() == providerType)
                    .filter(job -> job.getCircuitOpenUntil() != null)
                    .count();
            long activeJobs = jobs.stream()
                    .filter(job -> job.getProviderType() == providerType)
                    .count();
            Map<String, Object> providerDetails = new LinkedHashMap<>();
            providerDetails.put("activeJobs", activeJobs);
            providerDetails.put("openCircuits", openCircuits);
            details.put(providerType.name(), providerDetails);
        }
        return details;
    }

    @Override
    public Health health() {
        return Health.up().withDetails(snapshot()).build();
    }
}
