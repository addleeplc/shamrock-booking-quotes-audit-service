/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.caches;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.model.customer.Account;
import com.haulmont.shamrock.booking.quotes.audit.services.CustomerProfileService;
import org.apache.commons.lang3.ObjectUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ClientGradeCache implements CacheManagement {

    private static final long DEFAULT_CACHE_SIZE = 1000L;
    private static final long DEFAULT_CACHE_EXPIRATION_MINUTES = 60L;

    private LoadingCache<String, Optional<String>> clientGrade;

    @Inject
    private Logger logger;

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private CustomerProfileService customerProfileService;

    public void start() {
        clientGrade = CacheBuilder.newBuilder()
                .maximumSize(Optional.ofNullable(serviceConfiguration.getClientGradeCacheSize()).orElse(DEFAULT_CACHE_SIZE))
                .expireAfterWrite(ObjectUtils.defaultIfNull(serviceConfiguration.getClientGradeCacheElementExpireAfterMinutes(), DEFAULT_CACHE_EXPIRATION_MINUTES), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Optional<String> load(@Nonnull String key) {
                        Optional<Account> account = customerProfileService.getAccountByClient(key);
                        return account.map(value -> value.getGrade() != null ? value.getGrade().getCode() : null);
                    }
                });
    }

    public Optional<String> getGrade(String pid) {
        try {
            return clientGrade.get(pid);
        } catch (Exception e) {
            logger.warn("Failed to load client grade for pid: {}", pid, e);
            return Optional.empty();
        }
    }

    @Override
    public void invalidateAll() {
        clientGrade.invalidateAll();
    }
}
