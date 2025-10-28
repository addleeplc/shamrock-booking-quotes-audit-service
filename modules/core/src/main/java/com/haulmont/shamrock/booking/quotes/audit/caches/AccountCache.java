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
public class AccountCache implements CacheManagement {

    private static final long DEFAULT_CACHE_SIZE = 1000L;
    private static final long DEFAULT_CACHE_EXPIRATION_MINUTES = 60L;

    private LoadingCache<String, Optional<Account>> accountByClientPid;

    @Inject
    private Logger logger;

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private CustomerProfileService customerProfileService;

    public void start() {
        accountByClientPid = CacheBuilder.newBuilder()
                .maximumSize(Optional.ofNullable(serviceConfiguration.getAccountCacheSize()).orElse(DEFAULT_CACHE_SIZE))
                .expireAfterWrite(ObjectUtils.defaultIfNull(serviceConfiguration.getAccountCacheElementExpireAfterMinutes(), DEFAULT_CACHE_EXPIRATION_MINUTES), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Optional<Account> load(@Nonnull String key) {
                        return customerProfileService.getAccountByClient(key);
                    }
                });
    }

    public Optional<Account> getAccount(String clientPid) {
        try {
            return accountByClientPid.get(clientPid);
        } catch (Exception e) {
            logger.warn("Failed to load client for pid: {}", clientPid, e);
            return Optional.empty();
        }
    }

    @Override
    public void invalidateAll() {
        accountByClientPid.invalidateAll();
    }
}
