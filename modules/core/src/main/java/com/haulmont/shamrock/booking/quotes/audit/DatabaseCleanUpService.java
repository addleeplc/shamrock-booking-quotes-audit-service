package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.shamrock.booking.quotes.audit.mybatis.QuotationRepository;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.Optional;

@Component
//@Scheduled //todo uncomment
public class DatabaseCleanUpService {

    @Inject
    private ServiceConfiguration serviceConfiguration;

    @Inject
    private QuotationRepository quotationRepository;

//    @Schedule(schedule = ServiceConfiguration.DB_CLEAN_CHECK_RATE, delay = ServiceConfiguration.DB_CLEAN_CHECK_RATE)
    public void clean() {
        DateTime now = DateTime.now();
        quotationRepository.cleanUp(now.minusDays(getDays()));
    }

    private int getDays() {
        return Optional.ofNullable(serviceConfiguration.getDbCleanOlderThanDays()).orElse(30);
    }
}
