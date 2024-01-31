/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;

import java.util.List;
import java.util.UUID;

/**
 * Intermediate storage (cache) for quotes.
 */
public interface ProductQuotationRecordStorage {

    List<ProductQuotationRecord> get(UUID bookingId);

    List<ProductQuotationRecord> getAndRemove(UUID bookingId);

    void put(UUID bookingId, ProductQuotationRecord productQuotationRecord);

    void remove(UUID bookingId);

    List<UUID> keys();
}
