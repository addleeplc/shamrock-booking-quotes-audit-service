/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RestrictionCode {
    FULLY_BOOKED("fully_booked"),
    PUBLIC_EVENT("public_event"),
    BOOKING_POLICY("booking_policy"),
    PREBOOK_LIMIT("prebook_limit");

    private final String value;

    RestrictionCode(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static RestrictionCode fromValue(String value) {
        if (value != null) {
            for (RestrictionCode rts : RestrictionCode.values()) {
                if (rts.value.equalsIgnoreCase(value)) {
                    return rts;
                }
            }
        }
        return null;
    }
}
