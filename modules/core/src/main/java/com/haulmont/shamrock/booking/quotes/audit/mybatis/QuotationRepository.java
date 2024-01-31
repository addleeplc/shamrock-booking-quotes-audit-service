/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.mybatis;

import com.haulmont.monaco.mybatis.MyBatisCommand;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.BookingRecord;
import com.haulmont.shamrock.booking.quotes.audit.mybatis.entities.Quotation;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.picocontainer.annotations.Component;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

@Component
public class QuotationRepository {

    private static final String MYBATIS_NAMESPACE_PREFIX = "com.haulmont.shamrock.booking.quotes.audit";

    @Inject
    private Logger logger;

    @Inject
    private SqlSessionFactory sessionFactory;

    public void insertQuotation(Quotation quotation) {
        if (quotation == null) return;

        try {
            Integer res = new QuotationExistsCommand(quotation.getId()).execute() ?
                    new UpdateQuotationCommand(quotation).execute() :
                    new InsertQuotationCommand(quotation).execute();
            logger.debug("Successfully inserted {} rows", res);
        }  catch (Throwable t) {
            logger.error("Fail to insert quotation", t);
        }
    }

    public void insertBooking(BookingRecord bookingRecord) {
        if (bookingRecord == null) return;

        try {
            Integer res = new InsertBookingCommand(bookingRecord).execute();
            logger.debug("Successfully inserted {} rows", res);
        }  catch (Throwable t) {
            logger.error("Fail to insert booking", t);
        }
    }

    public boolean bookingExists(UUID bookingId) {
        if (bookingId == null) return false;
        try {
            return new BookingExistsCommand(bookingId).execute();
        }  catch (Throwable t) {
            logger.error("Fail to query booking (id: {})", bookingId);
            return false;
        }
    }

    private final class BookingExistsCommand extends MyBatisCommand<Boolean> {
        private final UUID bookingId;

        public BookingExistsCommand(UUID bookingId) {
            super(sessionFactory);
            this.bookingId = bookingId;
        }

        @Override
        protected Boolean __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(MYBATIS_NAMESPACE_PREFIX + "." + getName(),
                    Map.of("bookingId", bookingId));
        }

        @Override
        protected String getName() {
            return "bookingExists";
        }
    }

    private final class QuotationExistsCommand extends MyBatisCommand<Boolean> {
        private final UUID quotationId;

        public QuotationExistsCommand(UUID quotationId) {
            super(sessionFactory);
            this.quotationId = quotationId;
        }

        @Override
        protected Boolean __execute(SqlSession sqlSession) {
            return sqlSession.selectOne(MYBATIS_NAMESPACE_PREFIX + "." + getName(),
                    Map.of("quotationId", quotationId));
        }

        @Override
        protected String getName() {
            return "quotationExists";
        }
    }

    private final class InsertQuotationCommand extends MyBatisCommand<Integer> {
        private final Quotation quotation;

        public InsertQuotationCommand(Quotation quotation) {
            super(sessionFactory);
            this.quotation = quotation;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.insert(MYBATIS_NAMESPACE_PREFIX + "." + getName(), Map.of("quotation", quotation));
        }

        @Override
        protected String getName() {
            return "insertQuotation";
        }
    }

    private final class UpdateQuotationCommand extends MyBatisCommand<Integer> {
        private final Quotation quotation;

        public UpdateQuotationCommand(Quotation quotation) {
            super(sessionFactory);
            this.quotation = quotation;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.update(MYBATIS_NAMESPACE_PREFIX + "." + getName(), Map.of("quotation", quotation));
        }

        @Override
        protected String getName() {
            return "updateQuotation";
        }
    }

    private final class InsertBookingCommand extends MyBatisCommand<Integer> {
        private final BookingRecord bookingRecord;

        public InsertBookingCommand(BookingRecord bookingRecord) {
            super(sessionFactory);
            this.bookingRecord = bookingRecord;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.insert(MYBATIS_NAMESPACE_PREFIX + "." + getName(),
                    Map.of("booking", bookingRecord));
        }

        @Override
        protected String getName() {
            return "insertBooking";
        }
    }

    public void cleanUp(DateTime till) {
        if (till == null) return;

        try {
            Integer res = new CleanUpQuotationsCommand(till).execute();
            logger.debug("Successfully removed {} rows", res);
        } catch (Throwable t) {
            logger.error("Fail to clean-up old quotations", t);
        }
    }

    private final class CleanUpQuotationsCommand extends MyBatisCommand<Integer> {
        private final DateTime tillDate;

        public CleanUpQuotationsCommand(DateTime tillDate) {
            super(sessionFactory);
            this.tillDate = tillDate;
        }

        @Override
        protected Integer __execute(SqlSession sqlSession) {
            return sqlSession.delete(MYBATIS_NAMESPACE_PREFIX + "." + getName(), Map.of("till", tillDate));
        }

        @Override
        protected String getName() {
            return "removeOldQuotations";
        }
    }
}
