/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.PickupCircuit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PickupCircuitRestriction extends AbstractRestrictionMessage<PickupCircuitRestriction.Data> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractRestrictionMessage.Data {
        @JsonProperty("job_priority")
        private Integer jobPriority;

        @JsonProperty("pickup_circuit")
        private PickupCircuit pickupCircuit;

        public Integer getJobPriority() {
            return jobPriority;
        }

        public void setJobPriority(Integer jobPriority) {
            this.jobPriority = jobPriority;
        }

        public PickupCircuit getPickupCircuit() {
            return pickupCircuit;
        }

        public void setPickupCircuit(PickupCircuit pickupCircuit) {
            this.pickupCircuit = pickupCircuit;
        }
    }
}

