/**
 * Copyright (c) 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;

import java.util.List;

public class EventsResponse extends Response {

    private List<ProductQuotationRecord> events;

    public EventsResponse(List<ProductQuotationRecord> events, ErrorCode code) {
        super(code);
        this.events = events;
    }

    public List<ProductQuotationRecord> getEvents() {
        return events;
    }

    public void setEvents(List<ProductQuotationRecord> events) {
        this.events = events;
    }
}
