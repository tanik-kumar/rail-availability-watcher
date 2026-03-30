package com.example.railwatcher.api;

import com.example.railwatcher.api.dto.AlertResponse;
import com.example.railwatcher.api.dto.CreateWatchJobRequest;
import com.example.railwatcher.api.dto.ProviderErrorResponse;
import com.example.railwatcher.api.dto.StatusHistoryResponse;
import com.example.railwatcher.api.dto.WatchJobResponse;
import com.example.railwatcher.config.RailwayWatcherProperties;
import com.example.railwatcher.service.AvailabilityPollingService;
import com.example.railwatcher.service.WatchJobService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watchers")
public class WatchController {

    private final WatchJobService watchJobService;
    private final AvailabilityPollingService pollingService;
    private final RailwayWatcherProperties properties;

    public WatchController(
            WatchJobService watchJobService,
            AvailabilityPollingService pollingService,
            RailwayWatcherProperties properties
    ) {
        this.watchJobService = watchJobService;
        this.pollingService = pollingService;
        this.properties = properties;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchJobResponse create(@Valid @RequestBody CreateWatchJobRequest request) {
        return WatchJobResponse.from(watchJobService.create(request.toDomain(), properties.adminUser().id()));
    }

    @PutMapping("/{id}")
    public WatchJobResponse update(@PathVariable UUID id, @Valid @RequestBody CreateWatchJobRequest request) {
        return WatchJobResponse.from(watchJobService.update(id, request.toDomain(), properties.adminUser().id()));
    }

    @GetMapping
    public List<WatchJobResponse> list() {
        return watchJobService.list().stream().map(WatchJobResponse::from).toList();
    }

    @GetMapping("/{id}")
    public WatchJobResponse get(@PathVariable UUID id) {
        return WatchJobResponse.from(watchJobService.get(id));
    }

    @PatchMapping("/{id}/pause")
    public WatchJobResponse pause(@PathVariable UUID id) {
        return WatchJobResponse.from(watchJobService.pause(id));
    }

    @PatchMapping("/{id}/resume")
    public WatchJobResponse resume(@PathVariable UUID id) {
        return WatchJobResponse.from(watchJobService.resume(id));
    }

    @PostMapping("/{id}/check-now")
    public WatchJobResponse checkNow(@PathVariable UUID id) {
        pollingService.pollNow(id);
        return WatchJobResponse.from(watchJobService.get(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        watchJobService.delete(id);
    }

    @GetMapping("/{id}/history")
    public List<StatusHistoryResponse> history(@PathVariable UUID id) {
        return watchJobService.history(id).stream().map(StatusHistoryResponse::from).toList();
    }

    @GetMapping("/{id}/latest-status")
    public StatusHistoryResponse latestStatus(@PathVariable UUID id) {
        return watchJobService.history(id).stream()
                .findFirst()
                .map(StatusHistoryResponse::from)
                .orElseGet(() -> StatusHistoryResponse.from(watchJobService.get(id)));
    }

    @GetMapping("/{id}/alerts")
    public List<AlertResponse> alerts(@PathVariable UUID id) {
        return watchJobService.alerts(id).stream().map(AlertResponse::from).toList();
    }

    @GetMapping("/{id}/errors")
    public List<ProviderErrorResponse> errors(@PathVariable UUID id) {
        return watchJobService.providerErrors(id).stream().map(ProviderErrorResponse::from).toList();
    }

    @GetMapping("/aliases")
    public Map<String, Object> aliases() {
        return Map.of(
                "stations", properties.aliases().stations(),
                "trains", properties.aliases().trains()
        );
    }
}
