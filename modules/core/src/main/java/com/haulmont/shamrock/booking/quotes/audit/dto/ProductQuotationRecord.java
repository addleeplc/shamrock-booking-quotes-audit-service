/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductQuotationRecord implements Serializable {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("id")
    private UUID bookingId;

    @JsonProperty("booking_date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime bookingDate;

    @JsonProperty("booking_number")
    private String bookingNumber;

    @JsonProperty("booking_channel")
    private String bookingChannel;

    @JsonProperty("event_date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime eventDate;

    @JsonProperty("market")
    private Market market;

    @JsonProperty("asap")
    private Boolean asap;

    @JsonProperty("customer_code")
    private String customerCode;

    @JsonProperty("client_id")
    private UUID clientId;

    @JsonProperty("client_grade")
    private String clientGrade;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("pickup_address")
    private String pickupAddress;

    @JsonProperty("pickup_postcode")
    private String pickupPostcode;

    @JsonProperty("pickup_location_lat")
    private Double pickupLocationLat;

    @JsonProperty("pickup_location_lon")
    private Double pickupLocationLon;

    @JsonProperty("drop_address")
    private String dropAddress;

    @JsonProperty("drop_postcode")
    private String dropPostcode;

    @JsonProperty("drop_location_lat")
    private Double dropLocationLat;

    @JsonProperty("drop_location_lon")
    private Double dropLocationLon;

    @JsonProperty("response_time")
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class)
    private Period responseTime;

    @JsonProperty("response_time_source")
    private LeadTimeSource leadTimeSource;

    @JsonProperty("restriction_code")
    private RestrictionCode restrictionCode;

    @JsonProperty("restriction_message")
    private String restrictionMessage;

    @JsonProperty("within_public_event")
    private Boolean withinPublicEvent;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("create_date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime createDate;

    @JsonProperty("public_event_id")
    private String publicEventId;

    @JsonProperty("total_charged")
    private BigDecimal totalCharged;

    @JsonProperty("currency_code")
    private String currencyCode;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public DateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(DateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getBookingChannel() {
        return bookingChannel;
    }

    public void setBookingChannel(String bookingChannel) {
        this.bookingChannel = bookingChannel;
    }

    public DateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Boolean getAsap() {
        return asap;
    }

    public void setAsap(Boolean asap) {
        this.asap = asap;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getClientGrade() {
        return clientGrade;
    }

    public void setClientGrade(String clientGrade) {
        this.clientGrade = clientGrade;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getPickupPostcode() {
        return pickupPostcode;
    }

    public void setPickupPostcode(String pickupPostcode) {
        this.pickupPostcode = pickupPostcode;
    }

    public Double getPickupLocationLat() {
        return pickupLocationLat;
    }

    public void setPickupLocationLat(Double pickupLocationLat) {
        this.pickupLocationLat = pickupLocationLat;
    }

    public Double getPickupLocationLon() {
        return pickupLocationLon;
    }

    public void setPickupLocationLon(Double pickupLocationLon) {
        this.pickupLocationLon = pickupLocationLon;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getDropPostcode() {
        return dropPostcode;
    }

    public void setDropPostcode(String dropPostcode) {
        this.dropPostcode = dropPostcode;
    }

    public Double getDropLocationLat() {
        return dropLocationLat;
    }

    public void setDropLocationLat(Double dropLocationLat) {
        this.dropLocationLat = dropLocationLat;
    }

    public Double getDropLocationLon() {
        return dropLocationLon;
    }

    public void setDropLocationLon(Double dropLocationLon) {
        this.dropLocationLon = dropLocationLon;
    }

    public Period getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Period responseTime) {
        this.responseTime = responseTime;
    }

    public LeadTimeSource getLeadTimeSource() {
        return leadTimeSource;
    }

    public void setLeadTimeSource(LeadTimeSource leadTimeSource) {
        this.leadTimeSource = leadTimeSource;
    }

    public RestrictionCode getRestrictionCode() {
        return restrictionCode;
    }

    public void setRestrictionCode(RestrictionCode restrictionCode) {
        this.restrictionCode = restrictionCode;
    }

    public String getRestrictionMessage() {
        return restrictionMessage;
    }

    public void setRestrictionMessage(String restrictionMessage) {
        this.restrictionMessage = restrictionMessage;
    }

    public Boolean getWithinPublicEvent() {
        return withinPublicEvent;
    }

    public void setWithinPublicEvent(Boolean withinPublicEvent) {
        this.withinPublicEvent = withinPublicEvent;
    }

    public String getPublicEventId() {
        return publicEventId;
    }

    public void setPublicEventId(String publicEventId) {
        this.publicEventId = publicEventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getTotalCharged() {
        return totalCharged;
    }

    public void setTotalCharged(BigDecimal totalCharged) {
        this.totalCharged = totalCharged;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
