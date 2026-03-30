package com.example.railwatcher.provider;

import com.example.railwatcher.common.exception.ProviderException;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.common.model.WatchJob;
import com.example.railwatcher.config.RailwayWatcherProperties;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Placeholder HTTP provider.
 * Integrate only with a compliant, permitted data source. This component intentionally does not
 * contain any IRCTC login, CAPTCHA handling, or bot-protection workarounds.
 */
@Component
public class HttpPlaceholderAvailabilityProvider implements AvailabilityProvider {

    private final WebClient webClient;
    private final RailwayWatcherProperties properties;

    public HttpPlaceholderAvailabilityProvider(WebClient railwayWebClient, RailwayWatcherProperties properties) {
        this.webClient = railwayWebClient;
        this.properties = properties;
    }

    @Override
    public ProviderType providerType() {
        return ProviderType.HTTP_PLACEHOLDER;
    }

    @Override
    public TrainAvailabilitySnapshot fetchAvailability(WatchJob job) {
        if (!StringUtils.hasText(properties.provider().httpBaseUrl())) {
            throw new ProviderException(
                    "HTTP placeholder provider is not configured. Set railway.provider.http-base-url and implement a compliant data source.",
                    false
            );
        }

        HttpProviderResponse response = webClient.mutate()
                .baseUrl(properties.provider().httpBaseUrl())
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.provider().httpEndpointPath())
                        .queryParam("trainNumber", job.trainNumber())
                        .queryParam("journeyDate", job.journeyDate())
                        .queryParam("sourceStation", job.sourceStation())
                        .queryParam("destinationStation", job.destinationStation())
                        .queryParam("boardingStation", job.boardingStation())
                        .queryParam("quota", job.quota())
                        .queryParam("travelClass", job.travelClass())
                        .build())
                .retrieve()
                .bodyToMono(HttpProviderResponse.class)
                .switchIfEmpty(Mono.error(new ProviderException("Provider returned an empty body.", true)))
                .timeout(properties.provider().readTimeout())
                .onErrorMap(ex -> ex instanceof ProviderException
                        ? ex
                        : new ProviderException("Unable to fetch availability from HTTP placeholder provider.", true, ex))
                .block();

        if (response == null) {
            throw new ProviderException("Provider response was null.", true);
        }

        return new TrainAvailabilitySnapshot(
                ProviderType.HTTP_PLACEHOLDER,
                OffsetDateTime.now(),
                parseStatus(response.normalizedStatus()),
                response.rawStatus(),
                Boolean.TRUE.equals(response.bookingOpen()),
                Boolean.TRUE.equals(response.chartPrepared()),
                response.availableSeats(),
                response.responseTimeMs(),
                response.providerReference(),
                response.note()
        );
    }

    private NormalizedAvailabilityStatus parseStatus(String value) {
        if (!StringUtils.hasText(value)) {
            return NormalizedAvailabilityStatus.UNKNOWN;
        }
        try {
            return NormalizedAvailabilityStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return NormalizedAvailabilityStatus.UNKNOWN;
        }
    }

    private record HttpProviderResponse(
            String normalizedStatus,
            String rawStatus,
            Boolean bookingOpen,
            Boolean chartPrepared,
            Integer availableSeats,
            Long responseTimeMs,
            String providerReference,
            String note
    ) {
    }
}
