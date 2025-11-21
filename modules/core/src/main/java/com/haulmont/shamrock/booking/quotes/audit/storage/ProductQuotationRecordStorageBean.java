/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductQuotationRecordStorageBean implements ProductQuotationRecordStorage {

    @Inject
    private ServiceConfiguration configuration;

    @Inject
    private Logger logger;

    private ProductQuotationRecordStorage storage;

    public void start() {
        String redisResources = Optional.ofNullable(configuration.getRedisResource()).orElse("");
        if (redisResources.isEmpty()) {
            storage = new InteriorCache(configuration);
        } else {
            storage = new RedisCache(configuration, redisResources);
        }
    }

    @Override
    public List<ProductQuotationRecord> get(UUID bookingId) {
        return storage.get(bookingId);
    }

    @Override
    public List<ProductQuotationRecord> getAndRemove(UUID bookingId) {
        return storage.getAndRemove(bookingId);
    }

    @Override
    public void put(UUID bookingId, ProductQuotationRecord productQuotationRecord) {
        if (bookingId == null) {
            logger.warn("Fail to put a record to the intermediate storage because of the empty booking.id");
            return;
        }
        storage.put(bookingId, productQuotationRecord);
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
