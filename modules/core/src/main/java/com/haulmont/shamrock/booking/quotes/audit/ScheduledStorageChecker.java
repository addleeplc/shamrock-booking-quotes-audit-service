/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.scheduler.annotations.Schedule;
import com.haulmont.monaco.scheduler.annotations.Scheduled;
import com.haulmont.shamrock.booking.quotes.audit.storage.ProductQuotationRecordStorage;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

@Component
@Scheduled
public class ScheduledStorageChecker {

    @Inject
    private ProductQuotationRecordStorage productQuotationRecordStorage;

    @Inject
    private BookingQuotesAuditService bookingQuotesAuditService;

    @Inject
    private Logger logger;

    @Schedule(schedule = ServiceConfiguration.STORAGE_CHECK_RATE, delay = ServiceConfiguration.STORAGE_CHECK_RATE, singleton = true)
    public void check() {
        long start = System.currentTimeMillis();
        logger.info("Started scheduled intermediate storage check");
        try {
            for (UUID bookingId : productQuotationRecordStorage.keys()) {
                try {
                    bookingQuotesAuditService.checkRecordsFromIntermediateStorage(bookingId);
                } catch (Exception e) {
                    logger.warn("Fail to check booking from intermediate storage", e);
                }
            }
        } catch (Exception e) {
            logger.warn("Fail to load booking ids from intermediate storage", e);
        } finally {
            logger.info("Finished scheduled intermediate storage check in {} ms", System.currentTimeMillis() - start);
        }
    }
}
