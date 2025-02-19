/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.services;

import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.services.dto.booking_registry.BookingResponse;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import org.picocontainer.annotations.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class BookingRegistryService {
    private static final String SERVICE = "shamrock-booking-registry-service";

    public Booking getBooking(UUID id) {
        return ServiceCallUtils.call(
                () -> new GetBookingCommand(id),
                response -> ServiceCallUtils.extract(response, BookingResponse::getBooking));
    }

    private static final class GetBookingCommand extends UnirestCommand<BookingResponse> {
        private final UUID id;

        public GetBookingCommand(UUID id) {
            super(SERVICE, BookingResponse.class);
            this.id = id;
        }

        protected HttpRequest<GetRequest> createRequest(String s, Path path) {
            return get(s, path);
        }

        @Override
        protected Path getPath() {
            return new Path("/bookings/{bookingId}", Collections.singletonMap("bookingId", id));
        }
    }
}
