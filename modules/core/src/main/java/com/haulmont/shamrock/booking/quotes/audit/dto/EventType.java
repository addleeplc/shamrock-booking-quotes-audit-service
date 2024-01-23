/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

public enum EventType {
    PICKUP_CIRCUIT_RESTRICTION(true),
    JOB_CHECK_RESTRICTION(true),
    PUBLIC_EVENTS_RESTRICTION(true),
    PREBOOK_LIMIT_RESTRICTION(true),
    LEAD_TIME_QUOTED(false),
    BOOKING_CREATED(false),
    BOOKING_AMENDED(false),
    BOOKING_PRICED(false),
    SCHEDULED_TASK(false);

    private final boolean isRestriction;

    EventType(boolean isRestriction) {
        this.isRestriction = isRestriction;
    }

    public boolean isRestriction() {
        return isRestriction;
    }
}
