package com.example.railwatcher.provider;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.common.model.WatchJob;

public interface AvailabilityProvider {

    ProviderType providerType();

    TrainAvailabilitySnapshot fetchAvailability(WatchJob job);
}
