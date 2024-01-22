/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductAvailabilityRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class InteriorCache implements ProductAvailabilityRecordStorage {

    private final ServiceConfiguration configuration;

    private final Map<StorageKey, List<ProductAvailabilityRecord>> storage = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<StorageKey> priorityQueue = new PriorityBlockingQueue<>(5000, Comparator.comparing(StorageKey::getCreateDate));

    public InteriorCache(ServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<ProductAvailabilityRecord> get(UUID bookingId) {
        checkStorage();
        return storage.getOrDefault(new StorageKey(bookingId), Collections.emptyList());
    }

    @Override
    public void put(UUID bookingId, ProductAvailabilityRecord quotedResponseTime) {
        synchronized (this) {
            checkStorage();
            storage.compute(new StorageKey(bookingId), (key, quotedResponseTimes) -> {
                if (!priorityQueue.contains(key)) {
                    priorityQueue.add(key);
                }
                List<ProductAvailabilityRecord> list = new ArrayList<>();
                if (quotedResponseTimes != null) {
                    list.addAll(quotedResponseTimes);
                }
                list.add(quotedResponseTime);
                return list;
            });
        }
    }

    @Override
    public void remove(UUID bookingId) {
        StorageKey key = new StorageKey(bookingId);
        priorityQueue.remove(key);
        storage.remove(key);
    }

    @Override
    public List<UUID> keys() {
        checkStorage();
        Set<StorageKey> keys = storage.keySet();
        return keys.stream().map(StorageKey::getBookingId).collect(Collectors.toList());
    }

    private void checkStorage() {
        long removeAfter = configuration.getStorageExpireAfterMinutes() * 60 * 1000;
        long now = System.currentTimeMillis();

        List<StorageKey> keysForRemove = new ArrayList<>();
        boolean next = true;
        while (next) {
            StorageKey key = priorityQueue.peek();
            if (key == null) {
                next = false;
            } else {
                if (key.getCreateDate() + removeAfter < now) {
                    StorageKey keyForRemove = priorityQueue.poll();
                    if (keyForRemove != null) {
                        keysForRemove.add(keyForRemove);
                    } else {
                        next = false;
                    }
                } else {
                    next = false;
                }
            }
        }

        keysForRemove.forEach(storage::remove);
    }

    private static class StorageKey {
        private final UUID bookingId;
        private final Long createDate;

        public StorageKey(UUID bookingId) {
            this.bookingId = bookingId;
            this.createDate = System.currentTimeMillis();
        }

        public UUID getBookingId() {
            return bookingId;
        }

        public Long getCreateDate() {
            return createDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StorageKey that = (StorageKey) o;
            return bookingId.equals(that.bookingId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(bookingId);
        }
    }
}
