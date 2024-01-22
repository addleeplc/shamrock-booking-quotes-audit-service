/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.shamrock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LeadTimeSource {
    LEAD_TIME("LEAD_TIME"),
    FLEET("FLEET_RESPONSE_TIME"),
    SLA("SLA"),
    SUPPLIER("SUPPLIER");

    private final String value;

    LeadTimeSource(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static LeadTimeSource fromValue(String value) {
        if (value != null) {
            for (LeadTimeSource rts : LeadTimeSource.values()) {
                if (rts.value.equalsIgnoreCase(value)) {
                    return rts;
                }
            }
        }
        return null;
    }
}
