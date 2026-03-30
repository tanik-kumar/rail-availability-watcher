package com.example.railwatcher.common.model;

public enum NormalizedAvailabilityStatus {
    AVAILABLE,
    BOOKABLE,
    WAITLIST,
    NOT_AVAILABLE,
    REGRET,
    UNKNOWN,
    DEPARTED;

    public boolean isBookable() {
        return this == AVAILABLE || this == BOOKABLE;
    }
}
