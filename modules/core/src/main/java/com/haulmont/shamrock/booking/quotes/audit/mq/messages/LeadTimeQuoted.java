/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import org.joda.time.Period;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LeadTimeQuoted extends AbstractBookingMessage<LeadTimeQuoted.Data> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractBookingMessage.Data {
        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("time_estimate")
        @JsonDeserialize(using = DurationAdapter.Deserializer.class)
        @JsonSerialize(using = DurationAdapter.Serializer.class)
        private Period timeEstimate;

        @JsonProperty("time_estimate_source")
        private LeadTimeSource leadTimeSource;

        @JsonProperty("within_public_event")
        private Boolean withinPublicEvent;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public Period getTimeEstimate() {
            return timeEstimate;
        }

        public void setTimeEstimate(Period timeEstimate) {
            this.timeEstimate = timeEstimate;
        }

        public LeadTimeSource getLeadTimeSource() {
            return leadTimeSource;
        }

        public void setLeadTimeSource(LeadTimeSource leadTimeSource) {
            this.leadTimeSource = leadTimeSource;
        }

        public Boolean getWithinPublicEvent() {
            return withinPublicEvent;
        }

        public void setWithinPublicEvent(Boolean withinPublicEvent) {
            this.withinPublicEvent = withinPublicEvent;
        }
    }
}
