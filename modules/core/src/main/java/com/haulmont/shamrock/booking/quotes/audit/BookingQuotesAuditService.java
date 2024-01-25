/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.shamrock.booking.quotes.audit.config.GlobalConfigurationCache;
import com.haulmont.shamrock.booking.quotes.audit.dto.EventType;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;
import com.haulmont.shamrock.booking.quotes.audit.dto.RestrictionCode;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.EntitiesConverter;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.QuotationRepository;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.BookingRecord;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.Quotation;
import com.haulmont.shamrock.booking.quotes.audit.storage.ProductQuotationRecordStorage;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BookingQuotesAuditService {

    private static final List<EventType> RESTRICTION_EVENT_TYPES = List.of(
            EventType.JOB_CHECK_RESTRICTION, EventType.PICKUP_CIRCUIT_RESTRICTION,
            EventType.PREBOOK_LIMIT_RESTRICTION, EventType.PUBLIC_EVENTS_RESTRICTION);

    @Inject
    private Logger logger;

    @Inject
    private ProductQuotationRecordConverter productQuotationRecordConverter;

    @Inject
    private ProductQuotationRecordStorage productQuotationRecordStorage;

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

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking, eventDate, EventType.BOOKING_CREATED);

        saveBookingRecord(record);
    }

    public void processBookingAmended(Booking booking, DateTime eventDate) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking, eventDate, EventType.BOOKING_AMENDED);

        saveBookingRecord(record);
    }

    public void processBookingPriced(Booking booking, DateTime eventDate, BigDecimal totalCharged, String currencyCode) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking, eventDate, EventType.BOOKING_PRICED);
        record.setTotalCharged(totalCharged);
        record.setCurrencyCode(currencyCode);

        saveToIntermediateStorage(record);
    }

    public void processLeadTimeQuoted(Booking booking, DateTime eventDate, Period timeEstimate,
                                      LeadTimeSource leadTimeSource, Boolean withinPublicEvent,
                                      String transactionId) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking, eventDate, timeEstimate, leadTimeSource,
                withinPublicEvent, EventType.LEAD_TIME_QUOTED, transactionId);

        saveToIntermediateStorage(record);
    }

    public void processPickupCircuitRestriction(Booking booking, DateTime eventDate, String restrictionMessage) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
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

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
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

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
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

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking,
                eventDate,
                RestrictionCode.PREBOOK_LIMIT,
                restrictionMessage,
                EventType.PREBOOK_LIMIT_RESTRICTION
        );

        saveToIntermediateStorage(record);
    }

    private void saveToIntermediateStorage(ProductQuotationRecord record) {
        productQuotationRecordStorage.put(record.getBookingId(), record);
    }

    private void removeFromIntermediateStorage(ProductQuotationRecord record) {
        productQuotationRecordStorage.remove(record.getBookingId());
    }

    /**
     * Checks if intermediate storage contains other records related to the same booking
     * and saves the booking record to database.
     *
     * @param bookingRecord record related to created/amended booking
     */
    private void saveBookingRecord(ProductQuotationRecord bookingRecord) {
        logger.debug("Checking booking (id: {}) in the intermediate storage", bookingRecord.getBookingId());
        List<ProductQuotationRecord> quotes = productQuotationRecordStorage.get(bookingRecord.getBookingId());

        //add restriction info
        Optional<ProductQuotationRecord> restrictionRecord = quotes.stream()
                .filter(quote -> RESTRICTION_EVENT_TYPES.contains(quote.getEventType()))
                .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));

        if (restrictionRecord.isPresent()) {
            ProductQuotationRecord restriction = restrictionRecord.get();
            bookingRecord.setRestrictionCode(restriction.getRestrictionCode());
            bookingRecord.setRestrictionMessage(restriction.getRestrictionMessage());
            bookingRecord.setPublicEventId(restriction.getPublicEventId());
        }

        //add price info
        List<ProductQuotationRecord> priceRecords = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.BOOKING_PRICED)
                .collect(Collectors.toList());
        priceRecords.stream()
                .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                .ifPresent(record -> {
                    bookingRecord.setTotalCharged(record.getTotalCharged());
                    bookingRecord.setCurrencyCode(record.getCurrencyCode());
                });

        //collect last quotation
        Optional<ProductQuotationRecord> lastQuote = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            String transactionId = lastQuote.get().getTransactionId();
            if (StringUtils.isNotBlank(transactionId)) {
                List<ProductQuotationRecord> recordsBatch = quotes.stream()
                        .filter(qrt -> Objects.equals(qrt.getTransactionId(), transactionId))
                        .collect(Collectors.toList());

                if (recordsBatch.size() < 2) {
                    //try searching for not too old batch with the chosen product and more than 1 quote
                    long batchMaxAgeMilliseconds = configuration.getStorageBatchMaxAgeSeconds() * 1000;

                    Map<String, List<ProductQuotationRecord>> leadTimeQuotedBatches = quotes.stream()
                            .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .collect(Collectors.groupingBy(ProductQuotationRecord::getTransactionId));
                    Optional<List<ProductQuotationRecord>> lastAppropriateBatch = quotes.stream()
                            .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                            .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                            .filter(quote -> quote.getCreateDate().getMillis() + batchMaxAgeMilliseconds < System.currentTimeMillis())
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .filter(quote -> leadTimeQuotedBatches.get(quote.getTransactionId()).size() > 1)
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .map(productQuotationRecord -> leadTimeQuotedBatches.get(productQuotationRecord.getTransactionId()));

                    if (lastAppropriateBatch.isPresent()) {
                        recordsBatch = lastAppropriateBatch.get();
                    }
                }

                for (ProductQuotationRecord productQuote : recordsBatch) {
                    priceRecords.stream()
                            .filter(quote -> isRelevantForBooking(productQuote, quote))
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .ifPresent(record -> {
                                productQuote.setTotalCharged(record.getTotalCharged());
                                productQuote.setCurrencyCode(record.getCurrencyCode());
                            });
                }
                logger.debug("Saving booking (id: {}, number: {}) to the database",
                        bookingRecord.getBookingId(), bookingRecord.getBookingNumber());
                persistRecordsAsQuotation(recordsBatch);

                //if responseTime is null, fill it with the best quote's delay in the recent batch
                if (bookingRecord.getResponseTime() == null) {
                    Optional<ProductQuotationRecord> bestQuoteInTransaction =
                            recordsBatch.stream().min(Comparator.comparing(qrt -> qrt.getResponseTime().toStandardSeconds()));
                    ProductQuotationRecord bestQuote = bestQuoteInTransaction.orElseGet(lastQuote::get);
                    bookingRecord.setResponseTime(bestQuote.getResponseTime());
                }
                removeFromIntermediateStorage(bookingRecord);
            }
        } else {
            persistRecordsAsQuotation(Collections.singletonList(bookingRecord));
        }

        persistRecordAsBooking(bookingRecord);
    }

    private boolean isRelevantForBooking(ProductQuotationRecord bookingRecord,
                                         ProductQuotationRecord otherRecord) {
        return Objects.equals(bookingRecord.getProductId(), otherRecord.getProductId()) &&
                Objects.equals(bookingRecord.getPickupAddress(), otherRecord.getPickupAddress()) &&
                Objects.equals(bookingRecord.getDropAddress(), otherRecord.getDropAddress()) &&
                Objects.equals(bookingRecord.getAsap(), otherRecord.getAsap());
    }

    /**
     * Checks if booking with the given id is ready to be persisted.
     */
    public void checkRecordsFromIntermediateStorage(UUID bookingId) {
        logger.debug("Checking booking (id: {}) in the intermediate storage", bookingId);
        List<ProductQuotationRecord> quotes = productQuotationRecordStorage.get(bookingId);

        Optional<ProductQuotationRecord> lastQuote = quotes.stream()
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            long waitNextEventMilliseconds = configuration.getStorageWaitNextEventMinutes() * 60 * 1000;

            ProductQuotationRecord lastRecord = lastQuote.get();
            String transactionId = lastRecord.getTransactionId();
            List<ProductQuotationRecord> recordsBatch = null;
            if (StringUtils.isNotBlank(transactionId)) {
                recordsBatch = quotes.stream()
                        .filter(qrt -> Objects.equals(qrt.getTransactionId(), transactionId))
                        .collect(Collectors.toList());
                Optional<ProductQuotationRecord> bestQuoteInTransaction =
                        recordsBatch.stream()
                                .min(Comparator.comparing(qrt -> qrt.getResponseTime().toStandardSeconds()));
                lastRecord = bestQuoteInTransaction.orElseGet(lastQuote::get);
            }

            if ((lastRecord.getCreateDate().getMillis() + waitNextEventMilliseconds) < System.currentTimeMillis()) {
                if (lastRecord.getBookingDate() == null) {
                    Optional<DateTime> bookingDate = quotes.stream()
                            .sorted(Comparator.comparing(ProductQuotationRecord::getCreateDate).reversed())
                            .filter(qrt -> qrt.getBookingDate() != null)
                            .findFirst()
                            .map(ProductQuotationRecord::getBookingDate);
                    bookingDate.ifPresent(lastRecord::setBookingDate);
                }
                List<ProductQuotationRecord> quotations = recordsBatch != null
                        ? recordsBatch : Collections.singletonList(lastRecord);

                //add price info
                List<ProductQuotationRecord> priceRecords = quotes.stream()
                        .filter(quote -> quote.getEventType() == EventType.BOOKING_PRICED)
                        .collect(Collectors.toList());
                for (ProductQuotationRecord productQuote : quotations) {
                    priceRecords.stream()
                            .filter(quote -> isRelevantForBooking(productQuote, quote))
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .ifPresent(record -> {
                                productQuote.setTotalCharged(record.getTotalCharged());
                                productQuote.setCurrencyCode(record.getCurrencyCode());
                            });
                }

                logger.debug("Saving last quote for booking (id: {}) to the database", bookingId);
                persistRecordsAsQuotation(quotations);
                removeFromIntermediateStorage(lastRecord);
            }
        }
    }

    private void persistRecordsAsQuotation(List<ProductQuotationRecord> records) {
        Quotation quotation = EntitiesConverter.buildQuotation(records);
        quotationRepository.insertQuotation(quotation);
    }

    private void persistRecordAsBooking(ProductQuotationRecord record) {
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
