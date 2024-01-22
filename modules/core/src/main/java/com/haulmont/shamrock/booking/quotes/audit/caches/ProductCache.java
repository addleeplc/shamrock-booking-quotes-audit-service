/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.caches;

import com.haulmont.monaco.scheduler.annotations.Schedule;
import com.haulmont.monaco.scheduler.annotations.Scheduled;
import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.model.product.Product;
import com.haulmont.shamrock.booking.quotes.audit.services.ProductRegistryService;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Scheduled
public class ProductCache implements CacheManagement {

    private final Map<String, UUID> idByServiceCode = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Inject
    private ProductRegistryService productRegistryService;

    public void start() {
        refresh();
    }

    public Optional<UUID> getProductId(String serviceCode) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(idByServiceCode.get(serviceCode));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    @Schedule(schedule = ServiceConfiguration.PRODUCT_CACHE_CHECK_RATE, delay = ServiceConfiguration.PRODUCT_CACHE_CHECK_RATE)
    public void invalidateAll() {
        refresh();
    }

    private void refresh() {
        lock.writeLock().lock();
        try {
            List<Product> products = productRegistryService.getProducts();
            if (!products.isEmpty()) {
                idByServiceCode.clear();
                for (Product p : products) {
                    if (p.getServiceCodes() == null) {
                        continue;
                    }
                    for (String sc : p.getServiceCodes()) {
                        idByServiceCode.put(sc, p.getId());
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
