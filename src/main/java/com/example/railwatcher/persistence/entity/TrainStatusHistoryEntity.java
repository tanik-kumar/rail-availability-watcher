package com.example.railwatcher.persistence.entity;

import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "train_status_history")
public class TrainStatusHistoryEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watch_job_id", nullable = false)
    private WatchJobEntity watchJob;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "polled_at", nullable = false)
    private OffsetDateTime polledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "normalized_status", nullable = false)
    private NormalizedAvailabilityStatus normalizedStatus;

    @Column(name = "raw_status", nullable = false)
    private String rawStatus;

    @Column(name = "booking_open", nullable = false)
    private boolean bookingOpen;

    @Column(name = "chart_prepared", nullable = false)
    private boolean chartPrepared;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "provider_reference")
    private String providerReference;

    @Column(length = 2_000)
    private String note;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WatchJobEntity getWatchJob() {
        return watchJob;
    }

    public void setWatchJob(WatchJobEntity watchJob) {
        this.watchJob = watchJob;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public OffsetDateTime getPolledAt() {
        return polledAt;
    }

    public void setPolledAt(OffsetDateTime polledAt) {
        this.polledAt = polledAt;
    }

    public NormalizedAvailabilityStatus getNormalizedStatus() {
        return normalizedStatus;
    }

    public void setNormalizedStatus(NormalizedAvailabilityStatus normalizedStatus) {
        this.normalizedStatus = normalizedStatus;
    }

    public String getRawStatus() {
        return rawStatus;
    }

    public void setRawStatus(String rawStatus) {
        this.rawStatus = rawStatus;
    }

    public boolean isBookingOpen() {
        return bookingOpen;
    }

    public void setBookingOpen(boolean bookingOpen) {
        this.bookingOpen = bookingOpen;
    }

    public boolean isChartPrepared() {
        return chartPrepared;
    }

    public void setChartPrepared(boolean chartPrepared) {
        this.chartPrepared = chartPrepared;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
