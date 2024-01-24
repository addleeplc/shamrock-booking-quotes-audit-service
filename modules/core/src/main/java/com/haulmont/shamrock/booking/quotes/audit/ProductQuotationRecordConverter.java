/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.shamrock.booking.quotes.audit.caches.ClientGradeCache;
import com.haulmont.shamrock.booking.quotes.audit.caches.ProductCache;
import com.haulmont.shamrock.booking.quotes.audit.dto.EventType;
import com.haulmont.shamrock.booking.quotes.audit.dto.Market;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;
import com.haulmont.shamrock.booking.quotes.audit.dto.RestrictionCode;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Stop;
import com.haulmont.shamrock.booking.quotes.audit.model.product.Product;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductQuotationRecordConverter {

    @Inject
    private ClientGradeCache clientGradeCache;

    @Inject
    private ProductCache productCache;

    public ProductQuotationRecord createProductQuotationRecord(
            Booking booking,
            DateTime eventDate,
            EventType eventType) {
        return createProductQuotationRecord(
                booking,
                eventDate,
                null,
                null,
                null,
                null,
                null,
                eventType,
                null,
                null
        );
    }

    public ProductQuotationRecord createProductQuotationRecord(
            Booking booking,
            DateTime eventDate,
            RestrictionCode restrictionCode,
            String restrictionMessage,
            EventType eventType) {
        return createProductQuotationRecord(
                booking,
                eventDate,
                null,
                null,
                restrictionCode,
                restrictionMessage,
                null,
                eventType,
                null,
                null
        );
    }

    public ProductQuotationRecord createProductQuotationRecord(
            Booking booking,
            DateTime eventDate,
            RestrictionCode restrictionCode,
            String restrictionMessage,
            String publicEventId,
            EventType eventType) {
        return createProductQuotationRecord(
                booking,
                eventDate,
                null,
                null,
                restrictionCode,
                restrictionMessage,
                null,
                eventType,
                null,
                publicEventId
        );
    }

    public ProductQuotationRecord createProductQuotationRecord(
            Booking booking,
            DateTime eventDate,
            Period timeEstimate,
            LeadTimeSource leadTimeSource,
            Boolean withinPublicEvent,
            EventType eventType,
            String requestId) {
        return createProductQuotationRecord(
                booking,
                eventDate,
                timeEstimate,
                leadTimeSource,
                null,
                null,
                withinPublicEvent,
                eventType,
                requestId,
                null
        );
    }

    private ProductQuotationRecord createProductQuotationRecord(
            Booking booking,
            DateTime eventDate,
            Period timeEstimate,
            LeadTimeSource leadTimeSource,
            RestrictionCode restrictionCode,
            String restrictionMessage,
            Boolean withinPublicEvent,
            EventType eventType,
            String requestId,
            String publicEventId) {
        ProductQuotationRecord record = new ProductQuotationRecord();

        record.setTransactionId(getTransactionId(requestId));
        record.setBookingId(booking.getId());
        record.setBookingDate(booking.getDate());
        record.setBookingNumber(booking.getNumber());
        record.setBookingChannel(booking.getBookingChannel());
        record.setEventDate(eventDate);
        record.setMarket(getMarket(booking));
        record.setAsap(booking.getAsap());
        record.setCustomerCode(getCustomerCode(booking));
        record.setClientId(getClientId(booking));
        record.setClientGrade(getClientGrade(booking));
        record.setProductId(getProductId(booking));
        record.setProductCode(getProductCode(booking));
        record.setPickupAddress(getPickupAddress(booking));
        record.setPickupPostcode(getPickupPostcode(booking));
        record.setPickupLocationLat(getPickupLat(booking));
        record.setPickupLocationLon(getPickupLon(booking));
        record.setDropAddress(getDropAddress(booking));
        record.setDropPostcode(getDropPostcode(booking));
        record.setDropLocationLat(getDropLat(booking));
        record.setDropLocationLon(getDropLon(booking));
        record.setResponseTime(getResponseTime(booking, timeEstimate));
        record.setLeadTimeSource(leadTimeSource);
        record.setRestrictionCode(restrictionCode);
        record.setRestrictionMessage(restrictionMessage);
        record.setWithinPublicEvent(withinPublicEvent);
        record.setEventType(eventType);
        record.setCreateDate(DateTime.now());
        record.setPublicEventId(publicEventId);

        return record;
    }

    private Market getMarket(Booking booking) {
        if (booking.getCustomer() == null || booking.getCustomer().getCode() == null) {
            return null;
        }

        if (booking.getCustomer().getCode().startsWith("I|")) {
            return Market.B2C;
        }
        return Market.B2B;
    }

    private String getCustomerCode(Booking booking) {
        if (booking.getCustomer() != null) {
            return booking.getCustomer().getCode();
        }
        return null;
    }

    private UUID getClientId(Booking booking) {
        if (booking.getCustomerReference() != null && booking.getCustomerReference().getClient() != null) {
            return booking.getCustomerReference().getClient().getId();
        }
        return null;
    }

    private String getClientGrade(Booking booking) {
        if (booking.getCustomerReference() != null && booking.getCustomerReference().getClient() != null) {
            Optional<String> grade = clientGradeCache.getGrade(booking.getCustomerReference().getClient().getPid());
            if (grade.isPresent()) {
                return grade.get();
            }
        }
        return null;
    }

    private UUID getProductId(Booking booking) {
        String serviceCode = Optional.ofNullable(booking.getProduct()).map(Product::getCode).orElse(null);
        if (serviceCode == null) {
            return null;
        }

        return productCache.getProductId(serviceCode).orElse(null);
    }

    private String getProductCode(Booking booking) {
        return Optional.ofNullable(booking.getProduct()).map(Product::getCode).orElse(null);
    }

    private String getPickupAddress(Booking booking) {
        if (booking.getStops() != null && !booking.getStops().isEmpty()) {
            Stop stop = booking.getStops().get(0);
            return stop.getFormattedAddress();
        }
        return null;
    }

    private String getPickupPostcode(Booking booking) {
        if (booking.getStops() != null && !booking.getStops().isEmpty()) {
            Stop stop = booking.getStops().get(0);
            if (stop.getAddressComponents() != null) {
                return stop.getAddressComponents().getPostalCode();
            }
        }
        return null;
    }

    private Double getPickupLat(Booking booking) {
        if (booking.getStops() != null && !booking.getStops().isEmpty()) {
            Stop stop = booking.getStops().get(0);
            if (stop.getLocation() != null) {
                return stop.getLocation().getLat();
            }
        }
        return null;
    }

    private Double getPickupLon(Booking booking) {
        if (booking.getStops() != null && !booking.getStops().isEmpty()) {
            Stop stop = booking.getStops().get(0);
            if (stop.getLocation() != null) {
                return stop.getLocation().getLon();
            }
        }
        return null;
    }

    private String getDropAddress(Booking booking) {
        List<Stop> stops = booking.getStops();
        if (stops != null && !stops.isEmpty()) {
            Stop stop = stops.get(stops.size() - 1);
            return stop.getFormattedAddress();
        }
        return null;
    }

    private String getDropPostcode(Booking booking) {
        List<Stop> stops = booking.getStops();
        if (stops != null && !stops.isEmpty()) {
            Stop stop = stops.get(stops.size() - 1);
            if (stop.getAddressComponents() != null) {
                return stop.getAddressComponents().getPostalCode();
            }
        }
        return null;
    }

    private Double getDropLat(Booking booking) {
        List<Stop> stops = booking.getStops();
        if (stops != null && !stops.isEmpty()) {
            Stop stop = stops.get(stops.size() - 1);
            if (stop.getLocation() != null) {
                return stop.getLocation().getLat();
            }
        }
        return null;
    }

    private Double getDropLon(Booking booking) {
        List<Stop> stops = booking.getStops();
        if (stops != null && !stops.isEmpty()) {
            Stop stop = stops.get(stops.size() - 1);
            if (stop.getLocation() != null) {
                return stop.getLocation().getLon();
            }
        }
        return null;
    }

    private Period getResponseTime(Booking booking, Period responseTime) {
        return responseTime != null ? responseTime : booking.getPickupTimeEstimate();
    }

    private String getTransactionId(String transactionId) {
        if (StringUtils.isBlank(transactionId)) {
            return transactionId;
        }
        return transactionId.split("::")[0];
    }
}
