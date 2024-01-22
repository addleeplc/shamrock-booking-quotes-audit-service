/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.PublicEvent;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicEventsRestriction extends AbstractRestrictionMessage<PublicEventsRestriction.Data> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractRestrictionMessage.Data {

        private List<PublicEvent> publicEvents;

        @JsonProperty("public_events")
        public List<PublicEvent> getPublicEvents() {
            return publicEvents;
        }

        public void setPublicEvents(List<PublicEvent> publicEvents) {
            this.publicEvents = publicEvents;
        }
    }
}

