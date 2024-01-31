/**
 * Copyright (c) 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.booking.quotes.audit.dto.EventsResponse;
import com.haulmont.shamrock.booking.quotes.audit.dto.KeysResponse;
import com.haulmont.shamrock.booking.quotes.audit.storage.ProductQuotationRecordStorage;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/storage")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class StorageResources {

    @GET
    @Path("/")
    public Response getEvents() {
        return new KeysResponse(storage().keys(), ErrorCode.OK);
    }

    @GET
    @Path("/{bookingId}")
    public Response getEventDetailsByBooking(@PathParam("bookingId") UUID bookingId) {
        return new EventsResponse(storage().get(bookingId), ErrorCode.OK);
    }

    private static ProductQuotationRecordStorage storage() {
        return AppContext.getBean(ProductQuotationRecordStorage.class);
    }
}
