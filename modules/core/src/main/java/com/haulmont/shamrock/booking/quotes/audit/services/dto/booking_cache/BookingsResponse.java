/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.services.dto.booking_cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingsResponse extends Response {
    @JsonProperty("bookings")
    private List<Booking> bookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
