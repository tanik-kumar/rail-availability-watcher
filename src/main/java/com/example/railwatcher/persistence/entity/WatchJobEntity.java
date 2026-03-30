package com.example.railwatcher.persistence.entity;

import com.example.railwatcher.common.model.CircuitState;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.WatchJobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "watch_jobs")
public class WatchJobEntity extends AuditableEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderType providerType;

    @Column(name = "train_number", nullable = false)
    private String trainNumber;

    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @Column(name = "source_station", nullable = false)
    private String sourceStation;

    @Column(name = "destination_station", nullable = false)
    private String destinationStation;

    @Column(name = "boarding_station", nullable = false)
    private String boardingStation;

    @Column(nullable = false)
    private String quota;

    @Column(name = "travel_class", nullable = false)
    private String travelClass;

    @Column(name = "origin_departure_time", nullable = false)
    private LocalDateTime originDepartureTime;

    @Column(name = "boarding_departure_time")
    private LocalDateTime boardingDepartureTime;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WatchJobStatus status;

    @Column(name = "next_poll_at", nullable = false)
    private OffsetDateTime nextPollAt;

    @Column(name = "last_checked_at")
    private OffsetDateTime lastCheckedAt;

    @Column(name = "last_alert_at")
    private OffsetDateTime lastAlertAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private NormalizedAvailabilityStatus currentStatus;

    @Column(name = "current_raw_status")
    private String currentRawStatus;

    @Column(name = "booking_open", nullable = false)
    private boolean bookingOpen;

    @Column(name = "chart_prepared", nullable = false)
    private boolean chartPrepared;

    @Column(name = "available_seats")
    private Integer availableSeats;

    @Column(name = "notify_telegram", nullable = false)
    private boolean notifyTelegram;

    @Column(name = "notify_email", nullable = false)
    private boolean notifyEmail;

    @Column(name = "notify_webhook", nullable = false)
    private boolean notifyWebhook;

    @Column(length = 1_000)
    private String note;

    @Column(name = "consecutive_failures", nullable = false)
    private int consecutiveFailures;

    @Enumerated(EnumType.STRING)
    @Column(name = "circuit_state", nullable = false)
    private CircuitState circuitState;

    @Column(name = "circuit_open_until")
    private OffsetDateTime circuitOpenUntil;

    @Version
    private long version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getBoardingStation() {
        return boardingStation;
    }

    public void setBoardingStation(String boardingStation) {
        this.boardingStation = boardingStation;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    public LocalDateTime getOriginDepartureTime() {
        return originDepartureTime;
    }

    public void setOriginDepartureTime(LocalDateTime originDepartureTime) {
        this.originDepartureTime = originDepartureTime;
    }

    public LocalDateTime getBoardingDepartureTime() {
        return boardingDepartureTime;
    }

    public void setBoardingDepartureTime(LocalDateTime boardingDepartureTime) {
        this.boardingDepartureTime = boardingDepartureTime;
    }

    public LocalTime getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(LocalTime quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public LocalTime getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(LocalTime quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }

    public WatchJobStatus getStatus() {
        return status;
    }

    public void setStatus(WatchJobStatus status) {
        this.status = status;
    }

    public OffsetDateTime getNextPollAt() {
        return nextPollAt;
    }

    public void setNextPollAt(OffsetDateTime nextPollAt) {
        this.nextPollAt = nextPollAt;
    }

    public OffsetDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(OffsetDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public OffsetDateTime getLastAlertAt() {
        return lastAlertAt;
    }

    public void setLastAlertAt(OffsetDateTime lastAlertAt) {
        this.lastAlertAt = lastAlertAt;
    }

    public NormalizedAvailabilityStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(NormalizedAvailabilityStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentRawStatus() {
        return currentRawStatus;
    }

    public void setCurrentRawStatus(String currentRawStatus) {
        this.currentRawStatus = currentRawStatus;
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

    public boolean isNotifyTelegram() {
        return notifyTelegram;
    }

    public void setNotifyTelegram(boolean notifyTelegram) {
        this.notifyTelegram = notifyTelegram;
    }

    public boolean isNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public boolean isNotifyWebhook() {
        return notifyWebhook;
    }

    public void setNotifyWebhook(boolean notifyWebhook) {
        this.notifyWebhook = notifyWebhook;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(int consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }

    public CircuitState getCircuitState() {
        return circuitState;
    }

    public void setCircuitState(CircuitState circuitState) {
        this.circuitState = circuitState;
    }

    public OffsetDateTime getCircuitOpenUntil() {
        return circuitOpenUntil;
    }

    public void setCircuitOpenUntil(OffsetDateTime circuitOpenUntil) {
        this.circuitOpenUntil = circuitOpenUntil;
    }

    public long getVersion() {
        return version;
    }
}
