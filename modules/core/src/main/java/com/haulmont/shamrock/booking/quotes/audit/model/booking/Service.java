/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "tier",
        "products_direct_mapping",
        "products"
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Service {

    @JsonProperty("tier")
    private Integer serviceTier;

    @JsonProperty("products_direct_mapping")
    private boolean productsDirectMapping;

    public Integer getServiceTier() {
        return serviceTier;
    }

    public void setServiceTier(Integer serviceTier) {
        this.serviceTier = serviceTier;
    }

    public boolean isProductsDirectMapping() {
        return productsDirectMapping;
    }

    public void setProductsDirectMapping(boolean productsDirectMapping) {
        this.productsDirectMapping = productsDirectMapping;
    }
}
