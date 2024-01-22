/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class BookingAmended extends AbstractBookingMessage<BookingAmended.Data> {

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class Data extends AbstractBookingMessage.Data {
        @JsonProperty("changed_properties")
        Set<String> changedProperties;

        public Set<String> getChangedProperties() {
            return changedProperties;
        }

        public void setChangedProperties(Set<String> changedProperties) {
            this.changedProperties = changedProperties;
        }
    }
}
