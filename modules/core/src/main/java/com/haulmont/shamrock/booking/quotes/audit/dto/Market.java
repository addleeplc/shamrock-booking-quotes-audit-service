/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Market {
    B2B("B2B"),
    B2C("B2C");

    private final String value;

    Market(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static Market fromValue(String value) {
        if (value != null) {
            for (Market rts : Market.values()) {
                if (rts.value.equalsIgnoreCase(value)) {
                    return rts;
                }
            }
        }
        return null;
    }
}
