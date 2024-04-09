/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.services;

import com.haulmont.monaco.unirest.ServiceCallUtils;
import com.haulmont.monaco.unirest.UnirestCommand;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.services.dto.booking_cache.BookingsResponse;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.picocontainer.annotations.Component;

import java.util.UUID;

@Component
public class BookingCacheService {
    private static final String SERVICE = "shamrock-booking-cache";

    public Booking getBooking(UUID id) {
        return ServiceCallUtils.call(
                () -> new GetBookingCommand(id),
                response -> ServiceCallUtils.extract(response, o -> CollectionUtils.isNotEmpty(o.getBookings()) ? o.getBookings().get(0) : null));
    }

    private static final class GetBookingCommand extends UnirestCommand<BookingsResponse> {
        private final UUID id;

        public GetBookingCommand(UUID id) {
            super(SERVICE, BookingsResponse.class);
            this.id = id;
        }

        protected HttpRequest<GetRequest> createRequest(String s, Path path) {
            return get(s, path).queryString("id", id);
        }

        @Override
        protected Path getPath() {
            return new Path("/bookings");
        }
    }
}
