/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.scheduler.annotations.Schedule;
import com.haulmont.monaco.scheduler.annotations.Scheduled;
import com.haulmont.shamrock.booking.quotes.audit.dto.EventType;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductAvailabilityRecord;
import com.haulmont.shamrock.booking.quotes.audit.storage.ProductAvailabilityRecordStorage;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.List;
import java.util.UUID;

@Component
@Scheduled
public class ScheduledStorageChecker {

    @Inject
    private ProductAvailabilityRecordStorage productAvailabilityRecordStorage;

    @Inject
    private ProductAvailabilityAuditService productAvailabilityAuditService;

    @Schedule(schedule = ServiceConfiguration.STORAGE_CHECK_RATE, delay = ServiceConfiguration.STORAGE_CHECK_RATE)
    public void check() {
        List<UUID> ids = productAvailabilityRecordStorage.keys();

        for (UUID id : ids) {
            ProductAvailabilityRecord record = new ProductAvailabilityRecord();
            record.setBookingId(id);
            record.setEventType(EventType.SCHEDULED_TASK);
            productAvailabilityAuditService.checkRecordFromIntermediateStorage(record);
        }
    }
}
