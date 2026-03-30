package com.example.railwatcher.ui;

import com.example.railwatcher.common.model.ProviderType;
import com.example.railwatcher.common.model.WatchRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class WatcherForm {

    private UUID id;
    private UUID userId;
    private ProviderType providerType = ProviderType.MOCK;
    private String trainNumber;
    private LocalDate journeyDate;
    private String sourceStation;
    private String destinationStation;
    private String boardingStation;
    private String quota = "GN";
    private String travelClass = "3A";
    private LocalDateTime originDepartureTime;
    private LocalDateTime boardingDepartureTime;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private boolean notifyTelegram = true;
    private boolean notifyEmail = false;
    private boolean notifyWebhook = false;
    private String note;

    public static WatcherForm from(com.example.railwatcher.common.model.WatchJob watchJob) {
        WatcherForm form = new WatcherForm();
        form.setId(watchJob.id());
        form.setUserId(watchJob.userId());
        form.setProviderType(watchJob.providerType());
        form.setTrainNumber(watchJob.trainNumber());
        form.setJourneyDate(watchJob.journeyDate());
        form.setSourceStation(watchJob.sourceStation());
        form.setDestinationStation(watchJob.destinationStation());
        form.setBoardingStation(watchJob.boardingStation());
        form.setQuota(watchJob.quota());
        form.setTravelClass(watchJob.travelClass());
        form.setOriginDepartureTime(watchJob.originDepartureTime());
        form.setBoardingDepartureTime(watchJob.boardingDepartureTime());
        form.setQuietHoursStart(watchJob.quietHoursStart());
        form.setQuietHoursEnd(watchJob.quietHoursEnd());
        form.setNotifyTelegram(watchJob.notifyTelegram());
        form.setNotifyEmail(watchJob.notifyEmail());
        form.setNotifyWebhook(watchJob.notifyWebhook());
        form.setNote(watchJob.note());
        return form;
    }

    public WatchRequest toDomain() {
        return new WatchRequest(
                userId,
                providerType,
                trainNumber,
                journeyDate,
                sourceStation,
                destinationStation,
                boardingStation,
                quota,
                travelClass,
                originDepartureTime,
                boardingDepartureTime,
                quietHoursStart,
                quietHoursEnd,
                notifyTelegram,
                notifyEmail,
                notifyWebhook,
                note
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
}
