/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mybatis.entities;

import java.util.UUID;

public class ProductAvailability {
    private UUID id;
    private UUID quotationId;
    private String leadTime;
    private String leadTimeSource;
    private String restrictionCode;
    private String restrictionMessage;
    private UUID productId;
    private String productCode;
    private String publicEventId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(UUID quotationId) {
        this.quotationId = quotationId;
    }

    public String getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(String leadTime) {
        this.leadTime = leadTime;
    }

    public String getLeadTimeSource() {
        return leadTimeSource;
    }

    public void setLeadTimeSource(String leadTimeSource) {
        this.leadTimeSource = leadTimeSource;
    }

    public String getRestrictionCode() {
        return restrictionCode;
    }

    public void setRestrictionCode(String restrictionCode) {
        this.restrictionCode = restrictionCode;
    }

    public String getRestrictionMessage() {
        return restrictionMessage;
    }

    public void setRestrictionMessage(String restrictionMessage) {
        this.restrictionMessage = restrictionMessage;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPublicEventId() {
        return publicEventId;
    }

    public void setPublicEventId(String publicEventId) {
        this.publicEventId = publicEventId;
    }
}
