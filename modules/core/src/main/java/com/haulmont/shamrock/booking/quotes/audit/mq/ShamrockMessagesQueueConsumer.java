/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mq;

import com.haulmont.monaco.mq.annotations.Subscribe;
import com.haulmont.monaco.rabbit.mq.annotations.Consumer;
import com.haulmont.shamrock.booking.quotes.audit.ProductAvailabilityAuditService;
import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.model.booking.Booking;
import com.haulmont.shamrock.booking.quotes.audit.model.price.Price;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.LeadTimeSource;
import com.haulmont.shamrock.booking.quotes.audit.model.shamrock.PublicEvent;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.BookingAmended;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.BookingCreated;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.BookingPriced;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.JobCheckRestriction;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.LeadTimeQuoted;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.PickupCircuitRestriction;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.PrebookLimitRestriction;
import com.haulmont.shamrock.booking.quotes.audit.mq.messages.PublicEventsRestriction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.math.BigDecimal;

@Component
@Consumer(server = ServiceConfiguration.SHAMROCK_MQ_SERVER_NAME, queue = ServiceConfiguration.SHAMROCK_CONSUMER_PROPERTY_PREFIX)
public class ShamrockMessagesQueueConsumer {

    @Inject
    private Logger logger;

    @Inject
    private ProductAvailabilityAuditService productAvailabilityAuditService;

    @SuppressWarnings("unused")
    @Subscribe
    public void handleLeadTimeQuoted(LeadTimeQuoted message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();
            Period timeEstimate = message.getData().getTimeEstimate();
            LeadTimeSource responseTimeSource = message.getData().getLeadTimeSource();
            Boolean withinPublicEvent = message.getData().getWithinPublicEvent();
            String transactionId = message.getData().getTransactionId();

            productAvailabilityAuditService.processLeadTimeQuoted(booking, date, timeEstimate,
                    responseTimeSource, withinPublicEvent, transactionId);
        } catch (Exception e) {
            logger.error("Failed to process LeadTimeQuoted message (message.id: {})", message.getId(), e);
        }
    }

    @Subscribe
    public void handleBookingCreated(BookingCreated message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();

            productAvailabilityAuditService.processBookingCreated(booking, date);
        } catch (Exception e) {
            logger.error("Failed to process BookingCreated message (message.id: {})", message.getId(), e);
        }
    }

    @Subscribe
    public void handleBookingAmended(BookingAmended message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) return;

            DateTime date = message.getDate();
            productAvailabilityAuditService.processBookingAmended(booking, date);
        } catch (Exception e) {
            logger.error("Failed to process BookingAmended message (message.id: {})", message.getId(), e);
        }
    }

    @Subscribe
    public void handleBookingPriced(BookingPriced message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) return;

            Price price = message.getData().getPrice();
            BigDecimal totalCharged = price == null ? null : price.getTotalCharged();
            String currencyCode = price == null ? null : price.getCurrencyCode();

            DateTime date = message.getDate();
            productAvailabilityAuditService.processBookingPriced(booking, date, totalCharged, currencyCode);
        } catch (Exception e) {
            logger.error("Failed to process BookingPriced message (message.id: {})", message.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handlePickupCircuitRestriction(PickupCircuitRestriction message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();

            productAvailabilityAuditService.processPickupCircuitRestriction(booking, date, StringUtils.EMPTY);
        } catch (Exception e) {
            logger.error("Failed to process PickupCircuitRestriction message (message.id: {})", message.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handlePublicEventsRestriction(PublicEventsRestriction message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();

            String publicEventId = StringUtils.EMPTY;
            String publicEventName = StringUtils.EMPTY;
            if (CollectionUtils.isNotEmpty(message.getData().getPublicEvents())) {
                PublicEvent publicEvent = message.getData().getPublicEvents().iterator().next();
                publicEventId = publicEvent.getId();
                publicEventName = publicEvent.getName();
            }

            productAvailabilityAuditService.processPublicEventsRestriction(booking, date, publicEventName, publicEventId);
        } catch (Exception e) {
            logger.error("Failed to process PublicEventsRestriction message (message.id: {})", message.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleJobCheckRestriction(JobCheckRestriction message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();
            JobCheckRestriction.Data.Type restrictionType = message.getData().getType();
            String restrictionMessage = restrictionType != null ? restrictionType.toString() : StringUtils.EMPTY;

            productAvailabilityAuditService.processJobCheckRestriction(booking, date, restrictionMessage);
        } catch (Exception e) {
            logger.error("Failed to process JobCheckRestriction message (message.id: {})", message.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handlePrebookLimitRestriction(PrebookLimitRestriction message) {
        try {
            Booking booking = message.getData().getBooking();
            if (booking == null) {
                return;
            }

            DateTime date = message.getDate();

            productAvailabilityAuditService.processPrebookLimitRestriction(booking, date, StringUtils.EMPTY);
        } catch (Exception e) {
            logger.error("Failed to process PrebookLimitRestriction message (message.id: {})", message.getId(), e);
        }
    }
}
