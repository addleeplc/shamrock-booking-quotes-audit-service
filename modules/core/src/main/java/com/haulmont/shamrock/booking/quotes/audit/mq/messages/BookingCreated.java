/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingCreated extends AbstractBookingMessage<AbstractBookingMessage.Data> {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractBookingMessage.Data {
    }
}
