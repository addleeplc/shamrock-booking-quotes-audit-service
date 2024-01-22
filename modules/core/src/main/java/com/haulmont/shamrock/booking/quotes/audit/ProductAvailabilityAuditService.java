/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.shamrock.booking.quotes.audit.config.GlobalConfigurationCache;
import com.haulmont.shamrock.booking.quotes.audit.dto.EventType;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductAvailabilityRecord;
import com.haulmont.shamrock.booking.quotes.audit.dto.RestrictionCode;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.EntitiesConverter;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.QuotationRepository;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.BookingRecord;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.Quotation;
import com.haulmont.shamrock.booking.quotes.audit.storage.ProductAvailabilityRecordStorage;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductAvailabilityAuditService {

    private static final List<EventType> RESTRICTION_EVENT_TYPES = List.of(
            EventType.JOB_CHECK_RESTRICTION, EventType.PICKUP_CIRCUIT_RESTRICTION,
            EventType.PREBOOK_LIMIT_RESTRICTION, EventType.PUBLIC_EVENTS_RESTRICTION);

    @Inject
    private Logger logger;

    @Inject
    private ProductAvailabilityRecordConverter productAvailabilityRecordConverter;

    @Inject
    private ProductAvailabilityRecordStorage productAvailabilityRecordStorage;

    @Inject
    private QuotationRepository quotationRepository;

    @Inject
    private ServiceConfiguration configuration;

    @Inject
    private GlobalConfigurationCache globalConfigurationCache;

    public void processBookingCreated(Booking booking, DateTime eventDate) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking, eventDate, EventType.BOOKING_CREATED);

        checkIntermediateStorage(record);
    }

    public void processBookingAmended(Booking booking, DateTime eventDate) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking, eventDate, EventType.BOOKING_AMENDED);

        checkIntermediateStorage(record);
    }

    public void processLeadTimeQuoted(Booking booking, DateTime eventDate, Period timeEstimate,
                                      LeadTimeSource leadTimeSource, Boolean withinPublicEvent,
                                      String transactionId) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking, eventDate, timeEstimate, leadTimeSource,
                withinPublicEvent, EventType.LEAD_TIME_QUOTED, transactionId);

        saveToIntermediateStorage(record);
    }

    public void processPickupCircuitRestriction(Booking booking, DateTime eventDate, String restrictionMessage) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking,
                eventDate,
                RestrictionCode.FULLY_BOOKED,
                restrictionMessage,
                EventType.PICKUP_CIRCUIT_RESTRICTION
        );

        saveToIntermediateStorage(record);
    }

    public void processPublicEventsRestriction(Booking booking, DateTime eventDate,
                                               String restrictionMessage, String publicEventId) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking,
                eventDate,
                RestrictionCode.PUBLIC_EVENT,
                restrictionMessage,
                publicEventId,
                EventType.PUBLIC_EVENTS_RESTRICTION
        );

        saveToIntermediateStorage(record);
    }

    public void processJobCheckRestriction(Booking booking, DateTime eventDate, String restrictionMessage) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking,
                eventDate,
                RestrictionCode.BOOKING_POLICY,
                restrictionMessage,
                EventType.JOB_CHECK_RESTRICTION
        );

        saveToIntermediateStorage(record);
    }

    public void processPrebookLimitRestriction(Booking booking, DateTime eventDate, String restrictionMessage) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductAvailabilityRecord record = productAvailabilityRecordConverter.createProductAvailabilityRecord(
                booking,
                eventDate,
                RestrictionCode.PREBOOK_LIMIT,
                restrictionMessage,
                EventType.PREBOOK_LIMIT_RESTRICTION
        );

        saveToIntermediateStorage(record);
    }

    private void saveToIntermediateStorage(ProductAvailabilityRecord record) {
        productAvailabilityRecordStorage.put(record.getBookingId(), record);
    }

    private void removeFromIntermediateStorage(ProductAvailabilityRecord record) {
        productAvailabilityRecordStorage.remove(record.getBookingId());
    }

    /**
     * Check if intermediate storage contains other records related to the same booking.
     *
     * @param bookingRecord record related to created/amended booking
     */
    private void checkIntermediateStorage(ProductAvailabilityRecord bookingRecord) {
        logger.debug("Checking booking (id: {}) in the intermediate storage", bookingRecord.getBookingId());
        List<ProductAvailabilityRecord> quotes = productAvailabilityRecordStorage.get(bookingRecord.getBookingId());

        //add restriction info
        Optional<ProductAvailabilityRecord> restrictionRecord = quotes.stream()
                .filter(quote -> RESTRICTION_EVENT_TYPES.contains(quote.getEventType()))
                .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                .max(Comparator.comparing(ProductAvailabilityRecord::getCreateDate));

        if (restrictionRecord.isPresent()) {
            ProductAvailabilityRecord restriction = restrictionRecord.get();
            bookingRecord.setRestrictionCode(restriction.getRestrictionCode());
            bookingRecord.setRestrictionMessage(restriction.getRestrictionMessage());
            bookingRecord.setPublicEventId(restriction.getPublicEventId());
        }

        //collect last quotations
        Optional<ProductAvailabilityRecord> lastQuote = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                .max(Comparator.comparing(ProductAvailabilityRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            String transactionId = lastQuote.get().getTransactionId();
            if (StringUtils.isNotBlank(transactionId)) {
                List<ProductAvailabilityRecord> recordsBatch = quotes.stream()
                        .filter(qrt -> Objects.equals(qrt.getTransactionId(), transactionId))
                        .collect(Collectors.toList());

                logger.debug("Saving booking (id: {}, number: {}) to the database",
                        bookingRecord.getBookingId(), bookingRecord.getBookingNumber());
                persistRecordsAsQuotation(recordsBatch);

                //if responseTime is null, fill it with the best quote's delay in the recent batch
                if (bookingRecord.getResponseTime() == null) {
                    Optional<ProductAvailabilityRecord> bestQuoteInTransaction =
                            recordsBatch.stream().min(Comparator.comparing(qrt -> qrt.getResponseTime().toStandardSeconds()));
                    ProductAvailabilityRecord bestQuote = bestQuoteInTransaction.orElseGet(lastQuote::get);
                    bookingRecord.setResponseTime(bestQuote.getResponseTime());
                }
                removeFromIntermediateStorage(bookingRecord);
            }
        } else {
            persistRecordsAsQuotation(Collections.singletonList(bookingRecord));
        }

        persistRecordAsBooking(bookingRecord);
    }

    private boolean isRelevantForBooking(ProductAvailabilityRecord bookingRecord,
                                         ProductAvailabilityRecord restrictionRecord) {
        return Objects.equals(bookingRecord.getPickupAddress(), restrictionRecord.getPickupAddress()) &&
                Objects.equals(bookingRecord.getProductId(), restrictionRecord.getProductId()) &&
                Objects.equals(bookingRecord.getAsap(), restrictionRecord.getAsap());
    }

    /**
     * Check if the given record is ready to be persisted.
     */
    public void checkRecordFromIntermediateStorage(ProductAvailabilityRecord record) {
        logger.debug("Checking booking (id: {}) in the intermediate storage", record.getBookingId());
        List<ProductAvailabilityRecord> quotes = productAvailabilityRecordStorage.get(record.getBookingId());

        Optional<ProductAvailabilityRecord> lastQuote = quotes.stream()
                .max(Comparator.comparing(ProductAvailabilityRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            long waitNextEventMilliseconds = configuration.getStorageWaitNextEventMinutes() * 60 * 1000;

            ProductAvailabilityRecord lastRecord = lastQuote.get();
            String transactionId = lastRecord.getTransactionId();
            List<ProductAvailabilityRecord> recordsBatch = null;
            if (StringUtils.isNotBlank(transactionId)) {
                recordsBatch = quotes.stream()
                        .filter(qrt -> Objects.equals(qrt.getTransactionId(), transactionId))
                        .collect(Collectors.toList());
                Optional<ProductAvailabilityRecord> bestQuoteInTransaction =
                        recordsBatch.stream()
                                .min(Comparator.comparing(qrt -> qrt.getResponseTime().toStandardSeconds()));
                lastRecord = bestQuoteInTransaction.orElseGet(lastQuote::get);
            }

            if ((lastRecord.getCreateDate().getMillis() + waitNextEventMilliseconds) < System.currentTimeMillis()) {
                if (lastRecord.getBookingDate() == null) {
                    Optional<DateTime> bookingDate = quotes.stream()
                            .sorted(Comparator.comparing(ProductAvailabilityRecord::getCreateDate).reversed())
                            .filter(qrt -> qrt.getBookingDate() != null)
                            .findFirst()
                            .map(ProductAvailabilityRecord::getBookingDate);
                    bookingDate.ifPresent(lastRecord::setBookingDate);
                }
                List<ProductAvailabilityRecord> quotations = recordsBatch != null
                        ? recordsBatch : Collections.singletonList(lastRecord);
                logger.debug("Saving last quote for booking (id: {}) to the database", record.getBookingId());
                persistRecordsAsQuotation(quotations);
                removeFromIntermediateStorage(lastRecord);
            }
        }
    }

    private void persistRecordsAsQuotation(List<ProductAvailabilityRecord> records) {
        Quotation quotation = EntitiesConverter.buildQuotation(records);
        quotationRepository.insertQuotation(quotation);
    }

    private void persistRecordAsBooking(ProductAvailabilityRecord record) {
        BookingRecord booking = EntitiesConverter.buildBooking(record);
        quotationRepository.insertBooking(booking);
    }

    private boolean isAsapBooking(Booking booking) {
        return Boolean.TRUE.equals(booking.getAsap());
    }

    private boolean isTestAccount(Booking booking) {
        List<String> testAccounts = globalConfigurationCache.getTestAccounts();
        if (booking.getCustomer() != null && booking.getCustomer().getCode() != null) {
            return testAccounts.contains(booking.getCustomer().getCode());
        }
        return true;
    }
}
