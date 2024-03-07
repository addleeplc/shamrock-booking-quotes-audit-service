/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.config;

import com.haulmont.monaco.PropertyEvent;
import com.haulmont.monaco.config.Properties;
import org.apache.commons.collections4.CollectionUtils;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalConfigurationCache {

    private final static String TEST_ACCOUNT_PROPERTY_NAME = "system.testAccounts";

    private final Map<String, List<String>> testAccounts = new ConcurrentHashMap<>();

    @Inject
    private GlobalConfigurationStorage configurationStorage;

    public List<String> getTestAccounts() {
        List<String> value = testAccounts.get(TEST_ACCOUNT_PROPERTY_NAME);
        return CollectionUtils.isNotEmpty(value) ? new ArrayList<>(value) : Collections.emptyList();
    }

    public void start() {
        Properties properties = configurationStorage.getProperties("");
        String value = properties.getProperty(TEST_ACCOUNT_PROPERTY_NAME);

        cacheTestAccounts(value);

        configurationStorage.registerListener("", event -> {
            if (event.getType() == PropertyEvent.EventType.ADD || event.getType() == PropertyEvent.EventType.UPDATE) {
                String name = event.getKey();

                if (name.equals(TEST_ACCOUNT_PROPERTY_NAME)) {
                    cacheTestAccounts(event.getValue());
                }
            }
        });
    }

    private void cacheTestAccounts(String value) {
        testAccounts.put(TEST_ACCOUNT_PROPERTY_NAME, new ArrayList<>());

        if (value != null && !value.isEmpty()) {
            String[] accounts = value.split(",");
            for (String account : accounts) {
                if (!account.trim().isEmpty()) {
                    testAccounts.computeIfPresent(TEST_ACCOUNT_PROPERTY_NAME, (k, v) -> {
                        v.add(account.trim());
                        return v;
                    });
                }

            }
        }
    }
}