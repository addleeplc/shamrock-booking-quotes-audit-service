/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentModel {
    @JsonProperty("type")
    private Type type;

    @JsonProperty("frequency")
    private Frequency frequency;

    @JsonProperty("methods")
    private Collection<Method> methods;

    @JsonProperty("excess")
    private PaymentModel excess;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Collection<Method> getMethods() {
        return methods;
    }

    public void setMethods(Collection<Method> methods) {
        this.methods = methods;
    }

    public PaymentModel getExcess() {
        return excess;
    }

    public void setExcess(PaymentModel excess) {
        this.excess = excess;
    }

    public enum Type {
        ONE_TIME,
        RECURRING;

        @JsonValue
        public String value() {
            return super.name().toLowerCase();
        }
    }

    public enum Frequency {
        WEEKLY,
        DAILY,
        FORTHINGHLY,
        MONTHLY;

        @JsonValue
        public String value() {
            return super.name().toLowerCase();
        }
    }

    public enum Method {
        CASH,
        CREDIT_CARD,
        DIRECT_DEBIT,
        CHEQUES,
        BACS;

        @JsonValue
        public String value() {
            return super.name().toLowerCase();
        }
    }
}
