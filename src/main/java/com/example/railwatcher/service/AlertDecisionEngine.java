package com.example.railwatcher.service;

import com.example.railwatcher.common.model.AlertCandidate;
import com.example.railwatcher.common.model.AlertType;
import com.example.railwatcher.common.model.NormalizedAvailabilityStatus;
import com.example.railwatcher.common.model.TrainAvailabilitySnapshot;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AlertDecisionEngine {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public List<AlertCandidate> evaluate(
            WatchJobEntity job,
            NormalizedAvailabilityStatus previousStatus,
            boolean previousBookingOpen,
            boolean previousChartPrepared,
            TrainAvailabilitySnapshot current
    ) {
        List<AlertCandidate> alerts = new ArrayList<>();
        boolean strongAlertTriggered = false;

        if (!previousBookingOpen && current.bookingOpen()) {
            strongAlertTriggered = true;
            alerts.add(new AlertCandidate(
                    AlertType.BOOKING_OPEN,
                    "booking-open:" + current.fetchedAt().toLocalDate(),
                    title(job, "BOOKING OPEN"),
                    body(job, current, "Current booking appears to be open after chart preparation."),
                    true
            ));
        }

        if (!previousStatus.isBookable() && current.normalizedStatus().isBookable()) {
            strongAlertTriggered = true;
            alerts.add(new AlertCandidate(
                    AlertType.AVAILABLE,
                    "available:" + current.normalizedStatus(),
                    title(job, "SEAT AVAILABLE"),
                    body(job, current, "A bookable status is now visible."),
                    true
            ));
        }

        if (!previousChartPrepared && current.chartPrepared()) {
            alerts.add(new AlertCandidate(
                    AlertType.CHART_PREPARED,
                    "chart-prepared:" + current.fetchedAt().toLocalDate(),
                    title(job, "CHART PREPARED"),
                    body(job, current, "Chart preparation was detected or inferred for this watcher."),
                    false
            ));
        }

        if (previousStatus != current.normalizedStatus() && !strongAlertTriggered) {
            alerts.add(new AlertCandidate(
                    AlertType.STATUS_CHANGE,
                    "status-change:" + current.normalizedStatus(),
                    title(job, "STATUS CHANGED"),
                    body(job, current, "Availability status changed from " + previousStatus + " to " + current.normalizedStatus() + "."),
                    false
            ));
        }

        return alerts;
    }

    public AlertCandidate expired(WatchJobEntity job) {
        return new AlertCandidate(
                AlertType.WATCH_EXPIRED,
                "watch-expired:" + job.getJourneyDate(),
                title(job, "WATCH EXPIRED"),
                "Watcher " + job.getTrainNumber() + " " + job.getSourceStation() + " to " + job.getDestinationStation()
                        + " expired after the configured departure grace period.",
                false
        );
    }

    private String title(WatchJobEntity job, String suffix) {
        return job.getTrainNumber() + " " + job.getSourceStation() + "-" + job.getDestinationStation() + " " + suffix;
    }

    private String body(WatchJobEntity job, TrainAvailabilitySnapshot current, String message) {
        String seats = current.availableSeats() == null ? "-" : current.availableSeats().toString();
        return message
                + " Train " + job.getTrainNumber()
                + ", date " + job.getJourneyDate()
                + ", " + job.getBoardingStation() + " boarding"
                + ", class " + job.getTravelClass()
                + ", quota " + job.getQuota()
                + ", status " + current.rawStatus()
                + ", seats " + seats
                + ", checked " + DATE_TIME_FORMATTER.format(current.fetchedAt().toLocalDateTime()) + ".";
    }
}
