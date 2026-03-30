package com.example.railwatcher.persistence.repository;

import com.example.railwatcher.persistence.entity.AlertEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {

    List<AlertEntity> findTop50ByWatchJobIdOrderBySentAtDesc(UUID watchJobId);

    AlertEntity findTopByWatchJobIdOrderBySentAtDesc(UUID watchJobId);

    boolean existsByWatchJobIdAndDedupeKeyAndSentAtAfter(UUID watchJobId, String dedupeKey, OffsetDateTime sentAtAfter);
}
