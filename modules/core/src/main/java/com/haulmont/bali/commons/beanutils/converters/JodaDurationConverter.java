/*
 * Copyright 2008 - 2024 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.bali.commons.beanutils.converters;

import com.haulmont.monaco.scheduler.PeriodExpression;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Minutes;

public class JodaDurationConverter extends org.apache.commons.beanutils.converters.AbstractConverter {

    public JodaDurationConverter() {
        super(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (value == null) return null;

        if (value instanceof Duration) {
            return (T) value;
        } else if (value instanceof String) {
            String s = (String) value;
            if (StringUtils.isBlank(s)) return null;

            return (T) new Duration(PeriodExpression.parseExpression(s).getMillis());
        } else if (value instanceof Integer) {
            return (T) Minutes.minutes((Integer) value).toStandardDuration();
        } else if (value instanceof Long) {
            return (T) new Duration((Long) value);
        } else {
            throw new ConversionException(String.format("%s %s can't convert to Duration", value.getClass(), value.toString()));
        }
    }

    @Override
    protected String convertToString(Object value) {
        Duration v = (Duration) value;

        if (v == null) return null;
        return String.format("%sms", v.getMillis());
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) {
        return null;
    }

    @Override
    protected Class<?> getDefaultType() {
        return Duration.class;
    }
}
