/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.quotes.audit.model.driver.Driver;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Supplier {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("driver_ref")
    private Driver driverReference;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Driver getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(Driver driverReference) {
        this.driverReference = driverReference;
    }
}