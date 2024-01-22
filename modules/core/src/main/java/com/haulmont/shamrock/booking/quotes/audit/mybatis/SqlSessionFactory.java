/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mybatis;

import com.haulmont.monaco.mybatis.SqlSessionFactoryResource;
import org.picocontainer.annotations.Component;

@Component
public class SqlSessionFactory extends SqlSessionFactoryResource {
    public SqlSessionFactory() {
        super("shamrock-booking-quotes-audit-mybatis.xml");
    }
}
