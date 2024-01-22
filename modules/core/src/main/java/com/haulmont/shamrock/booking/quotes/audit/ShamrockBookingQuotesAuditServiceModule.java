package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.annotations.AfterStart;
import com.haulmont.monaco.annotations.Module;
import com.haulmont.monaco.container.ModuleLoader;

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
        packages("com.haulmont.shamrock.booking.quotes.audit");
    }

    @AfterStart
    public void postInit() {
        //Implement or remove if not needed
    }
}