/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductAvailabilityRecord;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductAvailabilityRecordStorageBean implements ProductAvailabilityRecordStorage {

    @Inject
    private ServiceConfiguration configuration;

    private ProductAvailabilityRecordStorage storage;

    public void start() {
        String redisResources = Optional.ofNullable(configuration.getRedisResource()).orElse("");
        if (redisResources.isEmpty()) {
            storage = new InteriorCache(configuration);
        } else {
            storage = new RedisCache(configuration, redisResources);
        }
    }

    @Override
    public List<ProductAvailabilityRecord> get(UUID bookingId) {
        return storage.get(bookingId);
    }

    @Override
    public void put(UUID bookingId, ProductAvailabilityRecord productAvailabilityRecord) {
        storage.put(bookingId, productAvailabilityRecord);
    }

    @Override
    public void remove(UUID bookingId) {
        storage.remove(bookingId);
    }

    @Override
    public List<UUID> keys() {
        return storage.keys();
    }
}
