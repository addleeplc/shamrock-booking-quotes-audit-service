/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.config;

import com.haulmont.monaco.config.GlobalConfigurationPropertyStorage;
import org.picocontainer.annotations.Component;

@Component
public class GlobalConfigurationStorage extends GlobalConfigurationPropertyStorage {
    public GlobalConfigurationStorage() {
        super("shamrock");
    }
}
