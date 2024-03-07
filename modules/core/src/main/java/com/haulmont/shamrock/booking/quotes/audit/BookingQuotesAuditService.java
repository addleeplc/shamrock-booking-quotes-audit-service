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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
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

        saveToIntermediateStorage(record);
    }

    public void processBookingAmended(Booking booking, DateTime eventDate) {
        if (isTestAccount(booking)) {
            return;
        }

        ProductQuotationRecord record = productQuotationRecordConverter.createProductQuotationRecord(
                booking, eventDate, EventType.BOOKING_AMENDED);

        saveToIntermediateStorage(record);
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

    private void removeFromIntermediateStorage(UUID bookingId) {
        productQuotationRecordStorage.remove(bookingId);
    }

    /**
     * Saves committed booking record to a database.
     *
     * @param bookingRecord record related to created/amended booking
     */
    private void saveBookingRecord(ProductQuotationRecord bookingRecord) {
        UUID bookingId = bookingRecord.getBookingId();
        List<ProductQuotationRecord> quotes = new ArrayList<>();
        for (ProductQuotationRecord quote : productQuotationRecordStorage.getAndRemove(bookingId)) {
            if (quote.getEventDate().getMillis() > bookingRecord.getEventDate().getMillis()) {
                productQuotationRecordStorage.put(bookingId, quote);
            } else {
                quotes.add(quote);
            }
        }

        List<ProductQuotationRecord> leadTimeRecords = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                .collect(Collectors.toList());

        List<ProductQuotationRecord> restrictionRecords = quotes.stream()
                .filter(quote -> RESTRICTION_EVENT_TYPES.contains(quote.getEventType()))
                .collect(Collectors.toList());

        List<ProductQuotationRecord> priceRecords = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.BOOKING_PRICED)
                .collect(Collectors.toList());

        addPriceInfo(bookingRecord, priceRecords);
        addRestrictionInfo(bookingRecord, restrictionRecords);

        //collect last quotation
        Optional<ProductQuotationRecord> lastQuote = leadTimeRecords.stream()
                .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            String transactionId = lastQuote.get().getTransactionId();
            if (StringUtils.isNotBlank(transactionId)) {
                List<ProductQuotationRecord> recordsBatch = quotes.stream()
                        .filter(qrt -> Objects.equals(qrt.getTransactionId(), transactionId))
                        .collect(Collectors.toList());

                if (recordsBatch.size() < 2) {
                    //try searching for not too old batch with the chosen product and more than 1 quote
                    long batchMaxAgeMilliseconds = configuration.getStorageBatchMaxAge().getMillis();

                    Map<String, List<ProductQuotationRecord>> leadTimeQuotedBatches = leadTimeRecords.stream()
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .collect(Collectors.groupingBy(ProductQuotationRecord::getTransactionId));
                    Optional<List<ProductQuotationRecord>> lastAppropriateBatch = leadTimeRecords.stream()
                            .filter(quote -> isRelevantForBooking(bookingRecord, quote))
                            .filter(quote -> quote.getCreateDate().getMillis() + batchMaxAgeMilliseconds >
                                    bookingRecord.getCreateDate().getMillis())
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .filter(quote -> leadTimeQuotedBatches.get(quote.getTransactionId()).size() > 1)
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .map(foundRecord -> leadTimeQuotedBatches.get(foundRecord.getTransactionId()));

                    if (lastAppropriateBatch.isPresent()) {
                        recordsBatch = lastAppropriateBatch.get();
                    }
                }

                for (ProductQuotationRecord productQuote : recordsBatch) {
                    addPriceInfo(productQuote, priceRecords);
                    addRestrictionInfo(productQuote, restrictionRecords);
                }
                recordsBatch = addMissingProductQuotations(recordsBatch, priceRecords, restrictionRecords);

                ProductQuotationRecord productQuote = recordsBatch.stream()
                        .filter(record -> Objects.equals(record.getProductId(), bookingRecord.getProductId()))
                        .findFirst()
                        .orElseGet(lastQuote::get);
                //final lead time might differ from the one in the selected batch
                if (!Objects.equals(bookingRecord.getResponseTime(), productQuote.getResponseTime())) {
                    productQuote.setResponseTime(lastQuote.get().getResponseTime());
                    productQuote.setLeadTimeSource(lastQuote.get().getLeadTimeSource());
                }

                logger.debug("Saving booking (id: {}, number: {}) to the database",
                        bookingId, bookingRecord.getBookingNumber());
                persistRecordsAsQuotation(recordsBatch);

                //if responseTime is null, fill it with the product quote's delay in the recent batch
                if (bookingRecord.getResponseTime() == null) {
                    bookingRecord.setResponseTime(productQuote.getResponseTime());
                }
                if (bookingRecord.getLeadTimeSource() == null
                        && Objects.equals(bookingRecord.getResponseTime(), productQuote.getResponseTime())) {
                    bookingRecord.setLeadTimeSource(productQuote.getLeadTimeSource());
                }
            }
        } else {
            logger.debug("Saving booking (id: {}, number: {}) to the database",
                    bookingId, bookingRecord.getBookingNumber());
            persistRecordsAsQuotation(Collections.singletonList(bookingRecord));
        }

        persistRecordAsBooking(bookingRecord);
    }

    private boolean isRelevantForBooking(ProductQuotationRecord originalRecord,
                                         ProductQuotationRecord otherRecord) {
        return Objects.equals(originalRecord.getProductId(), otherRecord.getProductId()) &&
                isEachStopRelevant(originalRecord, otherRecord) &&
                Objects.equals(originalRecord.getAsap(), otherRecord.getAsap());
    }

    private boolean isEachStopRelevant(ProductQuotationRecord originalRecord,
                                       ProductQuotationRecord otherRecord) {
        if (!Objects.equals(originalRecord.getPickupPostcode(), otherRecord.getPickupPostcode()) ||
                (!StringUtils.containsWhitespace(originalRecord.getPickupPostcode())
                        && !Objects.equals(originalRecord.getPickupAddress(), otherRecord.getPickupAddress()))) {
            return false;
        }
        //for public events we don't check drop address if it's not specified
        if (originalRecord.getEventType() == EventType.PUBLIC_EVENTS_RESTRICTION
                && StringUtils.isEmpty(originalRecord.getDropAddress()) ||
                otherRecord.getEventType() == EventType.PUBLIC_EVENTS_RESTRICTION
                        && StringUtils.isEmpty(otherRecord.getDropAddress())) {
            return true;
        }
        return Objects.equals(originalRecord.getDropPostcode(), otherRecord.getDropPostcode()) &&
                (StringUtils.containsWhitespace(originalRecord.getDropPostcode())
                        || Objects.equals(originalRecord.getDropAddress(), otherRecord.getDropAddress()));
    }

    private boolean isMarketRelevant(ProductQuotationRecord originalRecord,
                                     ProductQuotationRecord otherRecord) {
        //if original record does not have market info, skip this check
        return originalRecord.getMarket() == null || originalRecord.getMarket() == otherRecord.getMarket();
    }

    /**
     * Checks if booking with the given id is ready to be persisted.
     */
    public void checkRecordsFromIntermediateStorage(UUID bookingId) {
        logger.debug("Checking booking (id: {}) in the intermediate storage", bookingId);
        List<ProductQuotationRecord> quotes = productQuotationRecordStorage.get(bookingId);

        List<ProductQuotationRecord> bookingRecords = quotes.stream()
                .filter(quote -> quote.getEventType() == EventType.BOOKING_CREATED ||
                        quote.getEventType() == EventType.BOOKING_AMENDED)
                .collect(Collectors.toList());
        if (!bookingRecords.isEmpty()) {
            long bookingCreatedTimeoutMillis = configuration.getStorageWaitNextEventCommitted().getMillis();

            boolean bookingIsReadyToBeSaved = bookingRecords.stream()
                    .allMatch(record -> record.getCreateDate().getMillis() + bookingCreatedTimeoutMillis
                            < System.currentTimeMillis());
            if (bookingIsReadyToBeSaved) {
                bookingRecords.stream()
                        .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                        .ifPresent(this::saveBookingRecord);
            }
            return;
        }

        Optional<ProductQuotationRecord> lastQuote = quotes.stream()
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));

        if (lastQuote.isPresent()) {
            long waitNextEventMilliseconds = configuration.getStorageWaitNextEventNonCommitted().getMillis();

            if ((lastQuote.get().getCreateDate().getMillis() + waitNextEventMilliseconds) < System.currentTimeMillis()) {
                if (quotationRepository.bookingExists(bookingId)) {
                    logger.debug("Skipped saving last quotes for booking (id: {}) to the database", bookingId);
                    removeFromIntermediateStorage(bookingId);
                    return;
                }

                List<ProductQuotationRecord> leadTimeRecords = quotes.stream()
                        .filter(quote -> quote.getEventType() == EventType.LEAD_TIME_QUOTED)
                        .collect(Collectors.toList());
                List<ProductQuotationRecord> priceRecords = quotes.stream()
                        .filter(quote -> quote.getEventType() == EventType.BOOKING_PRICED)
                        .collect(Collectors.toList());
                List<ProductQuotationRecord> restrictionRecords = quotes.stream()
                        .filter(quote -> RESTRICTION_EVENT_TYPES.contains(quote.getEventType()))
                        .collect(Collectors.toList());

                ProductQuotationRecord lastRecord;
                Boolean asap;

                //in some cases last quote comes with asap = true, although in reality it was a prebook job
                //to check this we examine the last non lead time quote
                Optional<ProductQuotationRecord> lastNonLeadTimeQuote = priceRecords.stream()
                        .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));
                if (lastNonLeadTimeQuote.isEmpty()) {
                    lastNonLeadTimeQuote = restrictionRecords.stream()
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate));
                }
                if (lastNonLeadTimeQuote.isPresent()
                        && !Objects.equals(lastNonLeadTimeQuote.get().getAsap(), lastQuote.get().getAsap())) {
                    ProductQuotationRecord record = lastNonLeadTimeQuote.get();
                    asap = record.getAsap();
                    lastRecord = leadTimeRecords.stream()
                            .filter(quote -> Objects.equals(quote.getAsap(), asap))
                            .filter(quote -> isEachStopRelevant(record, quote))
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .orElse(record);
                } else {
                    lastRecord = lastQuote.get();
                    asap = lastRecord.getAsap();
                }

                List<ProductQuotationRecord> recordsBatch = null;
                String transactionId = lastRecord.getTransactionId();
                if (StringUtils.isNotBlank(transactionId)) {
                    recordsBatch = quotes.stream()
                            .filter(quote -> Objects.equals(quote.getTransactionId(), transactionId))
                            .collect(Collectors.toList());
                }

                if (recordsBatch == null || recordsBatch.size() < 2) {
                    //try searching for not too old batch with more than 1 quote
                    long batchMaxAgeMilliseconds = configuration.getStorageBatchMaxAge().getMillis();

                    Map<String, List<ProductQuotationRecord>> leadTimeQuotedBatches = leadTimeRecords.stream()
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .collect(Collectors.groupingBy(ProductQuotationRecord::getTransactionId));
                    Optional<List<ProductQuotationRecord>> lastAppropriateBatch = leadTimeRecords.stream()
                            .filter(quote -> quote.getCreateDate().getMillis() + batchMaxAgeMilliseconds >
                                    lastRecord.getCreateDate().getMillis())
                            .filter(quote -> Objects.equals(quote.getAsap(), asap))
                            .filter(quote -> isEachStopRelevant(lastRecord, quote))
                            .filter(quote -> isMarketRelevant(lastRecord, quote))
                            .filter(quote -> StringUtils.isNotBlank(quote.getTransactionId()))
                            .filter(quote -> leadTimeQuotedBatches.get(quote.getTransactionId()).size() > 1)
                            .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                            .map(foundRecord -> leadTimeQuotedBatches.get(foundRecord.getTransactionId()));

                    if (lastAppropriateBatch.isPresent()) {
                        recordsBatch = lastAppropriateBatch.get();
                    }
                }

                List<ProductQuotationRecord> productQuotations = recordsBatch != null
                        ? recordsBatch : Collections.singletonList(lastRecord);

                for (ProductQuotationRecord productQuote : productQuotations) {
                    addPriceInfo(productQuote, priceRecords);
                    addRestrictionInfo(productQuote, restrictionRecords);
                }

                //in some cases, single product quotation might be from price or restriction record.
                //if so, we are trying to populate it with lead time
                if (productQuotations.size() == 1 && productQuotations.get(0).getResponseTime() == null) {
                    addLeadTimeInfo(productQuotations.get(0), leadTimeRecords);
                }

                productQuotations = addMissingProductQuotations(productQuotations, priceRecords, restrictionRecords);

                //if booking date is null for some reason, take it from some other quote
                ProductQuotationRecord quotationRecord = productQuotations.get(0);
                if (quotationRecord.getBookingDate() == null) {
                    //first try to find it in the same batch
                    Optional<DateTime> bookingDate = productQuotations.stream()
                            .filter(qrt -> qrt.getBookingDate() != null)
                            .findFirst()
                            .map(ProductQuotationRecord::getBookingDate);
                    //otherwise try searching from all the quotes
                    if (bookingDate.isEmpty()) {
                        bookingDate = quotes.stream()
                                .sorted(Comparator.comparing(ProductQuotationRecord::getCreateDate).reversed())
                                .filter(qrt -> qrt.getBookingDate() != null)
                                .findFirst()
                                .map(ProductQuotationRecord::getBookingDate);
                    }
                    bookingDate.ifPresent(quotationRecord::setBookingDate);
                }

                logger.debug("Saving last quote for booking (id: {}) to the database", bookingId);
                persistRecordsAsQuotation(productQuotations);
                removeFromIntermediateStorage(bookingId);
            }
        }
    }

    /**
     * Tries to add quotations for the missing products to the {@code productQuotations} list
     * by searching in price and restriction records.
     */
    private List<ProductQuotationRecord> addMissingProductQuotations(List<ProductQuotationRecord> productQuotations,
                                                                     List<ProductQuotationRecord> priceRecords,
                                                                     List<ProductQuotationRecord> restrictionRecords) {

        if (productQuotations.isEmpty()) {
            return productQuotations;
        }
        ProductQuotationRecord productQuotation = productQuotations.get(0);

        Set<UUID> products = productQuotations.stream()
                .map(ProductQuotationRecord::getProductId)
                .collect(Collectors.toSet());

        //first examine price records
        Map<UUID, ProductQuotationRecord> newRecords = priceRecords.stream()
                .filter(record -> !products.contains(record.getProductId()))
                .filter(record -> isEachStopRelevant(productQuotation, record))
                .filter(record -> Objects.equals(productQuotation.getAsap(), record.getAsap()))
                .collect(Collectors.toMap(ProductQuotationRecord::getProductId, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ProductQuotationRecord::getCreateDate))));

        //then do the same with restriction records and merge the results
        restrictionRecords.stream()
                .filter(record -> !products.contains(record.getProductId()))
                .filter(record -> isEachStopRelevant(productQuotation, record))
                .filter(record -> Objects.equals(productQuotation.getAsap(), record.getAsap()))
                .collect(Collectors.toMap(ProductQuotationRecord::getProductId, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ProductQuotationRecord::getCreateDate))))
                .forEach((uuid, record) -> newRecords.merge(uuid, record, (record1, record2) -> {
                    record1.setRestrictionCode(record2.getRestrictionCode());
                    record1.setRestrictionMessage(record2.getRestrictionMessage());
                    record1.setPublicEventId(record2.getPublicEventId());
                    return record1;
                }));

        if (!newRecords.isEmpty()) {
            ArrayList<ProductQuotationRecord> result = new ArrayList<>(productQuotations);
            result.addAll(newRecords.values());
            return result;
        }
        return productQuotations;
    }

    private void addPriceInfo(ProductQuotationRecord record, List<ProductQuotationRecord> priceRecords) {
        if (record.getEventType() == EventType.BOOKING_PRICED) {
            return;
        }
        priceRecords.stream()
                .filter(quote -> isRelevantForBooking(record, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                .ifPresent(priceRecord -> {
                    record.setTotalCharged(priceRecord.getTotalCharged());
                    record.setCurrencyCode(priceRecord.getCurrencyCode());
                });
    }

    private void addRestrictionInfo(ProductQuotationRecord record, List<ProductQuotationRecord> restrictionRecords) {
        if (RESTRICTION_EVENT_TYPES.contains(record.getEventType())) {
            return;
        }
        restrictionRecords.stream()
                .filter(quote -> isRelevantForBooking(record, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                .ifPresent(restriction -> {
                    record.setRestrictionCode(restriction.getRestrictionCode());
                    record.setRestrictionMessage(restriction.getRestrictionMessage());
                    record.setPublicEventId(restriction.getPublicEventId());
                });
    }

    private void addLeadTimeInfo(ProductQuotationRecord record, List<ProductQuotationRecord> leadTimeRecords) {
        if (record.getEventType() == EventType.LEAD_TIME_QUOTED) {
            return;
        }
        leadTimeRecords.stream()
                .filter(quote -> isRelevantForBooking(record, quote))
                .max(Comparator.comparing(ProductQuotationRecord::getCreateDate))
                .ifPresent(leadTimeQuote -> {
                    record.setResponseTime(leadTimeQuote.getResponseTime());
                    record.setLeadTimeSource(leadTimeQuote.getLeadTimeSource());
                });
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
