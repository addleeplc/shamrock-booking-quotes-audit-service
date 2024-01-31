package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.bali.commons.beanutils.converters.JodaDurationConverter;
import com.haulmont.monaco.annotations.AfterStart;
import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;
import org.apache.commons.beanutils.ConvertUtils;
import org.joda.time.Duration;

@Module(name = "shamrock-booking-quotes-audit-service-module",
        depends = {
                "monaco-core",
                "monaco-ds",
                "monaco-ds-postgresql",
                "monaco-mybatis",
                "monaco-rabbit-mq",
                "monaco-redis-mq",
                "monaco-scheduler",
                "monaco-unirest",
                "monaco-sentry-reporter",
                "monaco-graylog-reporter"
        }
)
public class ShamrockBookingQuotesAuditServiceModule extends ModuleLoader {
    public ShamrockBookingQuotesAuditServiceModule() {
        super();
        ConvertUtils.register(new JodaDurationConverter(), Duration.class);
        packages("com.haulmont.shamrock.booking.quotes.audit");
    }

    @AfterStart
    public void postInit() {
        //Implement or remove if not needed
    }
}