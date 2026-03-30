package com.example.railwatcher.common.util;

import com.example.railwatcher.config.RailwayWatcherProperties;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class AliasResolver {

    private final RailwayWatcherProperties properties;

    public AliasResolver(RailwayWatcherProperties properties) {
        this.properties = properties;
    }

    public String resolveStation(String station) {
        return resolve(station, properties.aliases().stations());
    }

    public String resolveTrain(String train) {
        return resolve(train, properties.aliases().trains());
    }

    private String resolve(String value, java.util.Map<String, String> aliases) {
        if (value == null) {
            return null;
        }
        return aliases.getOrDefault(value.trim().toLowerCase(Locale.ROOT), value.trim().toUpperCase(Locale.ROOT));
    }
}
