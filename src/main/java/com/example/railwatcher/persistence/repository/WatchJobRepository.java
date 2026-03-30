package com.example.railwatcher.persistence.repository;

import com.example.railwatcher.common.model.WatchJobStatus;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchJobRepository extends JpaRepository<WatchJobEntity, UUID> {

    List<WatchJobEntity> findByStatusOrderByCreatedAtDesc(WatchJobStatus status);

    List<WatchJobEntity> findAllByOrderByCreatedAtDesc();

    List<WatchJobEntity> findTop50ByStatusAndNextPollAtLessThanEqualOrderByNextPollAtAsc(
            WatchJobStatus status,
            OffsetDateTime nextPollAt
    );
}
