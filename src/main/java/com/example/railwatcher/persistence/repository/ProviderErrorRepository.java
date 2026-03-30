package com.example.railwatcher.persistence.repository;

import com.example.railwatcher.persistence.entity.ProviderErrorEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderErrorRepository extends JpaRepository<ProviderErrorEntity, UUID> {

    List<ProviderErrorEntity> findTop50ByWatchJobIdOrderByOccurredAtDesc(UUID watchJobId);
}
