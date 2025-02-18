/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mybatis.entities;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Quotation implements Serializable {
    private UUID id;
    private DateTime createTs;
    private DateTime bookingDate;
    private String bookingChannel;
    private DateTime eventDate;
    private String market;
    private Boolean asap;
    private String customerCode;
    private UUID clientId;
    private String clientGrade;
    private String pickupAddress;
    private String pickupPostcode;
    private Double pickupLocationLat;
    private Double pickupLocationLon;
    private String dropAddress;
    private String dropPostcode;
    private Double dropLocationLat;
    private Double dropLocationLon;
    private String transactionId;
    private List<ProductQuotation> productQuotations;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DateTime getCreateTs() {
        return createTs;
    }

    public void setCreateTs(DateTime createTs) {
        this.createTs = createTs;
    }

    public DateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(DateTime bookingDate) {
        this.bookingDate = bookingDate;
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

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<ProductQuotation> getProductQuotations() {
        return productQuotations;
    }

    public void setProductQuotations(List<ProductQuotation> productQuotations) {
        this.productQuotations = productQuotations;
    }
}
