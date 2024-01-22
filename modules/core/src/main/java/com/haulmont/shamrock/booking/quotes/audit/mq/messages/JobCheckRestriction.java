/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobCheckRestriction extends AbstractBookingMessage<JobCheckRestriction.Data> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractBookingExecutionMessage.Data {
        @JsonProperty("type")
        private Type type;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public enum Type {
            LEAD_TIME_FAILED("LEAD_TIME_FAILED"),
            TELEPHONE_BANNED("TELEPHONE_BANNED"),
            EMAIL_BANNED("EMAIL_BANNED"),
            ADDRESS_BANNED("ADDRESS_BANNED");

            private final String value;
            private static final Map<String, Type> constants = new HashMap<>();

            static {
                for (Type c : values()) {
                    constants.put(c.value, c);
                }
            }

            Type(String value) {
                this.value = value;
            }

            @JsonValue
            @Override
            public String toString() {
                return this.value;
            }

            @JsonCreator
            public static Type fromValue(String value) {
                Type constant = constants.get(value);
                if (constant == null) {
                    throw new IllegalArgumentException(value);
                } else {
                    return constant;
                }
            }
        }
    }


}
