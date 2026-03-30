package com.example.railwatcher.persistence.repository;

import com.example.railwatcher.persistence.entity.TrainStatusHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainStatusHistoryRepository extends JpaRepository<TrainStatusHistoryEntity, UUID> {

    List<TrainStatusHistoryEntity> findTop100ByWatchJobIdOrderByPolledAtDesc(UUID watchJobId);

    TrainStatusHistoryEntity findTopByWatchJobIdOrderByPolledAtDesc(UUID watchJobId);
}
