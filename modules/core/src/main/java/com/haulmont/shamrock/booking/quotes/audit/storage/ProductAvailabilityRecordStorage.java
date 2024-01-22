/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.shamrock.booking.quotes.audit.dto.ProductAvailabilityRecord;

import java.util.List;
import java.util.UUID;

/**
 * Intermediate storage (cache) for quotes.
 */
public interface ProductAvailabilityRecordStorage {

    List<ProductAvailabilityRecord> get(UUID bookingId);

    void put(UUID bookingId, ProductAvailabilityRecord productAvailabilityRecord);

    void remove(UUID bookingId);

    List<UUID> keys();
}
