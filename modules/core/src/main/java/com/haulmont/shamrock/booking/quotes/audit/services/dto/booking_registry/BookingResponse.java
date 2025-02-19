/*
 * Copyright 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.shamrock.booking.quotes.audit.services.dto.booking_registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse extends Response {
    @JsonProperty("booking")
    private Booking booking;

    public BookingResponse() {}

    public BookingResponse(ErrorCode code) {
        super(code.getCode(), code.getMessage());
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
