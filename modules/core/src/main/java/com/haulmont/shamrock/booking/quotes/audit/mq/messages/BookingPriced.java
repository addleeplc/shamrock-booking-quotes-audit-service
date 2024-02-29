/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.jackson.annotations.SensitiveData;
import com.haulmont.shamrock.booking.quotes.audit.model.price.Explanation;
import com.haulmont.shamrock.booking.quotes.audit.model.price.Price;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class BookingPriced extends AbstractBookingMessage<BookingPriced.Data> {
    public static class Data extends AbstractBookingMessage.Data {
        @JsonProperty("price")
        private Price price;

        @JsonProperty("explanation")
        @SensitiveData
        private Explanation explanation;

        @JsonProperty("pricing_server")
        private String pricingServer;

        @JsonProperty("pricing_server_version")
        private String pricingVersion;

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }

        public Explanation getExplanation() {
            return explanation;
        }

        public void setExplanation(Explanation explanation) {
            this.explanation = explanation;
        }

        public String getPricingServer() {
            return pricingServer;
        }

        public void setPricingServer(String pricingServer) {
            this.pricingServer = pricingServer;
        }

        public String getPricingVersion() {
            return pricingVersion;
        }

        public void setPricingVersion(String pricingVersion) {
            this.pricingVersion = pricingVersion;
        }
    }
}
