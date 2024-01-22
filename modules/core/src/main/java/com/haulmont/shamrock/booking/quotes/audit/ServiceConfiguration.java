package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import org.picocontainer.annotations.Component;

@Config
@Component
public interface ServiceConfiguration {
    String SHAMROCK_MQ_SERVER_NAME = "mq.shamrock-events.serverName";
    String SHAMROCK_CONSUMER_PROPERTY_PREFIX = "mq.shamrock-events";

    String STORAGE_CHECK_RATE = "storage.check-rate";
    String DB_CLEAN_CHECK_RATE = "db.clean.check-rate";

    String PRODUCT_CACHE_CHECK_RATE = "caches.products.check-rate";

    @Property("redis.resourceName")
    String getRedisResource();

    @Property("caches.client-grade.size")
    Long getClientGradeCacheSize();

    @Property("caches.client-grade.expire-after.minutes")
    Long getClientGradeCacheElementExpireAfterMinutes();

    @Property("storage.wait-next-event.minutes")
    Long getStorageWaitNextEventMinutes();

    @Property("storage.expire-after.minutes")
    Long getStorageExpireAfterMinutes();

    @Property("db.clean.older-than.days")
    Integer getDbCleanOlderThanDays();
}
