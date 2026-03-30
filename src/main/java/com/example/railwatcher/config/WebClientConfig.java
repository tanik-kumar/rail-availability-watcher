package com.example.railwatcher.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    WebClient railwayWebClient(WebClient.Builder builder, RailwayWatcherProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(properties.provider().connectTimeout().toMillis()))
                .doOnConnected(connection -> connection.addHandlerLast(
                        new ReadTimeoutHandler(properties.provider().readTimeout().toMillis(), TimeUnit.MILLISECONDS)));

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("User-Agent", properties.provider().userAgent())
                .build();
    }
}
