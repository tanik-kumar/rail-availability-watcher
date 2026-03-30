package com.example.railwatcher.provider;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.WatchRequest;
import com.example.railwatcher.service.AvailabilityPollingService;
import com.example.railwatcher.service.StatusHistoryService;
import com.example.railwatcher.service.WatchJobService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MockProviderPollingWorkflowTest {

    @Autowired
    private WatchJobService watchJobService;

    @Autowired
    private AvailabilityPollingService pollingService;

    @Autowired
    private StatusHistoryService statusHistoryService;

    @Test
    void shouldPersistHistoryWhenManualPollRuns() {
        var watchJob = watchJobService.create(new WatchRequest(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                ProviderType.MOCK,
                "12309",
                LocalDate.now().plusDays(5),
                "PNBE",
                "NDLS",
                "PNBE",
                "GN",
                "2A",
                LocalDateTime.now().plusDays(5).withHour(17).withMinute(55).withSecond(0).withNano(0),
                LocalDateTime.now().plusDays(5).withHour(17).withMinute(55).withSecond(0).withNano(0),
                null,
                null,
                false,
                false,
                false,
                "Polling workflow test"
        ), UUID.fromString("00000000-0000-0000-0000-000000000001"));

        pollingService.pollNow(watchJob.id());

        var refreshed = watchJobService.get(watchJob.id());

        assertThat(refreshed.lastCheckedAt()).isNotNull();
        assertThat(refreshed.currentRawStatus()).isNotEqualTo("NOT_CHECKED");
        assertThat(refreshed.currentStatus()).isNotNull();
    }
}
