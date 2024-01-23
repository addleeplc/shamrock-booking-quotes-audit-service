/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.monaco.App;
import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.HostInfo;
import com.haulmont.monaco.Version;
import com.haulmont.monaco.jackson.annotations.SensitiveData;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.model.price.Explanation;
import com.haulmont.shamrock.booking.quotes.audit.model.price.Price;
import org.joda.time.DateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class BookingPriced extends AbstractBookingMessage<BookingPriced.Data> {
    public static BookingPriced build(Booking booking, Price price, Explanation explanation) {
        BookingPriced o = new BookingPriced();

        o.setDate(DateTime.now());
        if (price != null) {
            o.setDescription(String.format("Booking priced as %.2f %s", price.getTotalCharged(), price.getCurrencyCode()));
        } else {
            o.setDescription("Booking will be priced on completion");
        }

        Data data = new Data();

        data.setBooking(booking);
        data.setPrice(price);
        data.setExplanation(explanation);

        HostInfo host = AppContext.getHost();
        Version version = App.getVersion();

        data.setPricingServer(host.getHostName());
        data.setPricingVersion(Version.MASTER.equals(version) ? null : (version.getVersion()));

        o.setData(data);

        return o;
    }

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
