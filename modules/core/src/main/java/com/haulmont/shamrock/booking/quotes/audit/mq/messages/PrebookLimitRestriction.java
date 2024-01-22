/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.PrebookLimit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrebookLimitRestriction extends AbstractRestrictionMessage<PrebookLimitRestriction.Data> {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends AbstractRestrictionMessage.Data {

        @JsonProperty("prebook_limit")
        private PrebookLimit prebookLimit;

        public PrebookLimit getPrebookLimit() {
            return prebookLimit;
        }

        public void setPrebookLimit(PrebookLimit prebookLimit) {
            this.prebookLimit = prebookLimit;
        }
    }
}

