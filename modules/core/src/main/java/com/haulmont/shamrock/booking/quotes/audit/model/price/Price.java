/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.price;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Period;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({
        "id",
        "currency_code",
        "base_fare",
        "waiting_fare",
        "waiting_duration",
        "adjustments",
        "discount",
        "taxes",
        "total_charged",
        "duration",
        "distance",
        "distance_units"
})
public class Price implements Serializable {
    @JsonProperty("id")
    private String id;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("base_fare")
    private BigDecimal baseFare;

    @JsonProperty("waiting_fare")
    private BigDecimal waitingFare;
    /**
     * mm:HH:ss
     */
    @JsonProperty("waiting_duration")
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private Period waitingDuration;

    @JsonProperty("discount")
    private BigDecimal discount;

//    @JsonProperty("adjustments")
//    private List<Adjustment> adjustments = new ArrayList<Adjustment>();

    @JsonProperty("total_charged")
    private BigDecimal totalCharged;

    /**
     * mm:HH:ss
     */
    @JsonProperty("duration")
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private Period duration;

    @JsonProperty("distance")
    private Double distance;

    @JsonProperty("distance_units")
    private DistanceUnits distanceUnits;

//    @JsonProperty("taxes")
//    private Collection<TaxItem> taxes = new ArrayList<>();

    //

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getWaitingFare() {
        return waitingFare;
    }

    public void setWaitingFare(BigDecimal waitingFare) {
        this.waitingFare = waitingFare;
    }

    public Period getWaitingDuration() {
        return waitingDuration;
    }

    public void setWaitingDuration(Period waitingDuration) {
        this.waitingDuration = waitingDuration;
    }

    public BigDecimal getTotalCharged() {
        return totalCharged;
    }

    public void setTotalCharged(BigDecimal totalCharged) {
        this.totalCharged = totalCharged;
    }

    public Period getDuration() {
        return duration;
    }
    public void setDuration(Period duration) {
        this.duration = duration;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public DistanceUnits getDistanceUnits() {
        return distanceUnits;
    }
    public void setDistanceUnits(DistanceUnits distanceUnits) {
        this.distanceUnits = distanceUnits;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public enum DistanceUnits {

        MILES("MILES"),
        METERS("METERS");

        private final String value;
        private static final Map<String, DistanceUnits> constants = new HashMap<String, DistanceUnits>();

        static {
            for (DistanceUnits c: values()) {
                constants.put(c.value, c);
            }
        }

        DistanceUnits(String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static DistanceUnits fromValue(String value) {
            if (StringUtils.isBlank(value)) return null;

            DistanceUnits constant = constants.get(StringUtils.upperCase(value));
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public static class Item {
        @JsonProperty(value = "code")
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

//    public static class TaxItem extends Tax {
//        @JsonProperty("source")
//        private String source;
//
//        @JsonProperty("amount")
//        private BigDecimal amount;
//
//        public String getSource() {
//            return source;
//        }
//
//        public void setSource(String source) {
//            this.source = source;
//        }
//
//        public BigDecimal getAmount() {
//            return amount;
//        }
//
//        public void setAmount(BigDecimal amount) {
//            this.amount = amount;
//        }
//    }

    //

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Price withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }
}
