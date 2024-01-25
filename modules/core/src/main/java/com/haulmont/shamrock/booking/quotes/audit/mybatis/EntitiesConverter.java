/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mybatis;

import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;
import com.haulmont.shamrock.booking.quotes.audit.dto.RestrictionCode;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.BookingRecord;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.ProductQuotation;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.Quotation;
import org.joda.time.Period;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntitiesConverter {

    private static String periodToInterval(Period period) {
        StringBuilder interval = new StringBuilder();

        if (period == null) {
            return null;
        }

        if (period.getDays() > 0) {
            interval.append(period.getDays()).append(" days");
        }
        if (period.getHours() > 0) {
            interval.append(" ").append(period.getHours()).append(" hours");
        }
        if (period.getMinutes() > 0) {
            interval.append(" ").append(period.getMinutes()).append(" minutes");
        }
        if (period.getSeconds() > 0) {
            interval.append(" ").append(period.getSeconds()).append(" seconds");
        }

        return !interval.toString().trim().isEmpty() ? interval.toString().trim() : null;
    }

    public static Quotation buildQuotation(List<ProductQuotationRecord> records) {
        if (records == null || records.isEmpty()) {
            return null;
        }
        ProductQuotationRecord record = records.get(0);
        Quotation quotation = new Quotation();
        quotation.setId(record.getBookingId());
        quotation.setBookingDate(record.getBookingDate());
        quotation.setBookingChannel(record.getBookingChannel());
        quotation.setEventDate(record.getEventDate());
        quotation.setMarket(record.getMarket().toString());
        quotation.setAsap(record.getAsap());
        quotation.setCustomerCode(record.getCustomerCode());
        quotation.setClientId(record.getClientId());
        quotation.setClientGrade(record.getClientGrade());
        quotation.setPickupAddress(record.getPickupAddress());
        quotation.setPickupPostcode(record.getPickupPostcode());
        quotation.setPickupLocationLat(record.getPickupLocationLat());
        quotation.setPickupLocationLon(record.getPickupLocationLon());
        quotation.setDropAddress(record.getDropAddress());
        quotation.setDropPostcode(record.getDropPostcode());
        quotation.setDropLocationLat(record.getDropLocationLat());
        quotation.setDropLocationLon(record.getDropLocationLon());
        quotation.setTransactionId(record.getTransactionId());

        List<ProductQuotation> productQuotations =
                records.stream().map(EntitiesConverter::buildProductQuotation).collect(Collectors.toList());
        quotation.setProductQuotations(productQuotations);

        return quotation;
    }

    public static ProductQuotation buildProductQuotation(ProductQuotationRecord record) {
        ProductQuotation productQuotation = new ProductQuotation();
        productQuotation.setId(UUID.randomUUID());
        productQuotation.setQuotationId(record.getBookingId());
        productQuotation.setLeadTime(periodToInterval(record.getResponseTime()));
        productQuotation.setLeadTimeSource(Optional.ofNullable(record.getLeadTimeSource()).map(LeadTimeSource::toString).orElse(null));
        productQuotation.setRestrictionCode(Optional.ofNullable(record.getRestrictionCode()).map(RestrictionCode::toString).orElse(null));
        productQuotation.setRestrictionMessage(record.getRestrictionMessage());
        productQuotation.setProductId(record.getProductId());
        productQuotation.setProductCode(record.getProductCode());
        productQuotation.setPrice(record.getTotalCharged());
        productQuotation.setCurrencyCode(record.getCurrencyCode());
        productQuotation.setPublicEventId(record.getPublicEventId());
        return productQuotation;
    }

    public static BookingRecord buildBooking(ProductQuotationRecord record) {
        BookingRecord booking = new BookingRecord();
        booking.setId(record.getBookingId());
        booking.setQuotationId(record.getBookingId());
        booking.setBookingNumber(record.getBookingNumber());
        booking.setLeadTime(periodToInterval(record.getResponseTime()));
        booking.setLeadTimeSource(Optional.ofNullable(record.getLeadTimeSource()).map(LeadTimeSource::toString).orElse(null));
        booking.setRestrictionCode(Optional.ofNullable(record.getRestrictionCode()).map(RestrictionCode::toString).orElse(null));
        booking.setRestrictionMessage(record.getRestrictionMessage());
        booking.setProductId(record.getProductId());
        booking.setProductCode(record.getProductCode());
        booking.setPrice(record.getTotalCharged());
        booking.setCurrencyCode(record.getCurrencyCode());
        booking.setPublicEventId(record.getPublicEventId());
        return booking;
    }
}
