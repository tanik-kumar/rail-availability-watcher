package com.example.railwatcher.provider;

import com.example.railwatcher.common.model.ProviderType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityProviderRegistry {

    private final Map<ProviderType, AvailabilityProvider> providers = new EnumMap<>(ProviderType.class);

    public AvailabilityProviderRegistry(List<AvailabilityProvider> providers) {
        providers.forEach(provider -> this.providers.put(provider.providerType(), provider));
    }

    public AvailabilityProvider getProvider(ProviderType providerType) {
        AvailabilityProvider provider = providers.get(providerType);
        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for " + providerType);
        }
        return provider;
    }
}
