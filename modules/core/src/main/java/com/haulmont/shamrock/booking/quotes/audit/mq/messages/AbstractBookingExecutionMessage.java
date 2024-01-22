/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.quotes.audit.model.driver.Driver;

public abstract class AbstractBookingExecutionMessage<T extends AbstractBookingExecutionMessage.Data>
        extends AbstractBookingMessage<T> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractBookingMessage.Data {
        @JsonProperty("driver")
        Driver driver;

        public Driver getDriver() {
            return driver;
        }

        public void setDriver(Driver driver) {
            this.driver = driver;
        }
    }
}

