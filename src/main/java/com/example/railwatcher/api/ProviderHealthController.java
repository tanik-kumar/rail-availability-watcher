package com.example.railwatcher.api;

import com.example.railwatcher.service.ProviderHealthService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class ProviderHealthController {

    private final ProviderHealthService providerHealthService;

    public ProviderHealthController(ProviderHealthService providerHealthService) {
        this.providerHealthService = providerHealthService;
    }

    @GetMapping("/provider")
    public Map<String, Object> providerHealth() {
        return providerHealthService.snapshot();
    }
}
