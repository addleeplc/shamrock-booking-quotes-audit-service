/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.booking;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.haulmont.shamrock.booking.quotes.audit.model.commons.AddressComponents;
import com.haulmont.shamrock.booking.quotes.audit.model.commons.Location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({
        "type",
        "operation",
        "location",
        "flight"
})
public class Stop implements Serializable {

    @JsonProperty("type")
    private Type type = Type.ADDRESS;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    @JsonProperty("address_components")
    private AddressComponents addressComponents;

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("flight")
    private Flight flight;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public AddressComponents getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(AddressComponents addressComponents) {
        this.addressComponents = addressComponents;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public enum Type {

        ADDRESS("ADDRESS"),
        AIRPORT("AIRPORT"),
        TRAIN_STATION("TRAIN_STATION");
        private static Map<String, Type> constants = new HashMap<String, Type>();

        static {
            for (Type c : values()) {
                constants.put(c.value, c);
            }
        }

        private final String value;

        private Type(String value) {
            this.value = value;
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

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

    }

}