/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.booking;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import com.haulmont.bali.jackson.joda.DurationAdapter;
import com.haulmont.shamrock.booking.quotes.audit.model.driver.Driver;
import com.haulmont.shamrock.booking.quotes.audit.model.product.Product;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Booking implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("pid")
    private String pid;

    @JsonProperty("number")
    private String number;

    @JsonProperty("booking_date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime bookingDate;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("customer_reference")
    private CustomerReference customerReference;

    @JsonProperty("product")
    private Product product;

    @JsonProperty("number_of_passengers")
    private Integer numberOfPassengers;

    @JsonProperty("as_directed")
    private Boolean asDirected;

    @JsonProperty("destination_unknown")
    private Boolean destinationUnknown;

    @JsonProperty("wait_and_return")
    private Boolean waitAndReturn;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("asap")
    private Boolean asap;

    @JsonProperty("date")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime date;

    @JsonProperty("stops")
    private List<Stop> stops = new ArrayList<>();

    @JsonProperty("driver")
    private Driver driver;

    @JsonProperty("preallocated_driver")
    private Driver preallocatedDriver;

    @JsonProperty("prebooked_by_driver")
    private Driver prebookedByDriver;

    @JsonProperty("booking_channel")
    private String bookingChannel;

    @JsonProperty("pickup_time_estimate")
    @JsonDeserialize(using = DurationAdapter.Deserializer.class)
    @JsonSerialize(using = DurationAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private Period pickupTimeEstimate;

    @JsonProperty("supplier")
    private Supplier supplier;

    public DateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(DateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerReference getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(CustomerReference customerReference) {
        this.customerReference = customerReference;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Boolean getAsap() {
        return asap;
    }

    public void setAsap(Boolean asap) {
        this.asap = asap;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public Integer getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(Integer numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public Boolean getAsDirected() {
        return asDirected;
    }

    public void setAsDirected(Boolean asDirected) {
        this.asDirected = asDirected;
    }

    public Boolean getDestinationUnknown() {
        return destinationUnknown;
    }

    public void setDestinationUnknown(Boolean destinationUnknown) {
        this.destinationUnknown = destinationUnknown;
    }

    public Boolean getWaitAndReturn() {
        return waitAndReturn;
    }

    public void setWaitAndReturn(Boolean waitAndReturn) {
        this.waitAndReturn = waitAndReturn;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Driver getPreallocatedDriver() {
        return preallocatedDriver;
    }

    public void setPreallocatedDriver(Driver preallocatedDriver) {
        this.preallocatedDriver = preallocatedDriver;
    }

    public Driver getPrebookedByDriver() {
        return prebookedByDriver;
    }

    public void setPrebookedByDriver(Driver prebookedByDriver) {
        this.prebookedByDriver = prebookedByDriver;
    }

    public String getBookingChannel() {
        return bookingChannel;
    }

    public void setBookingChannel(String bookingChannel) {
        this.bookingChannel = bookingChannel;
    }

    public Period getPickupTimeEstimate() {
        return pickupTimeEstimate;
    }

    public void setPickupTimeEstimate(Period pickupTimeEstimate) {
        this.pickupTimeEstimate = pickupTimeEstimate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}