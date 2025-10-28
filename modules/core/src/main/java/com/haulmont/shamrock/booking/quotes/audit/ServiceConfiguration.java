package com.haulmont.shamrock.booking.quotes.audit;

import com.haulmont.monaco.config.annotations.Config;
import com.haulmont.monaco.config.annotations.Property;
import org.joda.time.Duration;
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

    @Property("caches.account.size")
    Long getAccountCacheSize();

    @Property("caches.account.expire-after.minutes")
    Long getAccountCacheElementExpireAfterMinutes();

    @Property("storage.wait-next-event.committed")
    Duration getStorageWaitNextEventCommitted();

    @Property("storage.wait-next-event.non-committed")
    Duration getStorageWaitNextEventNonCommitted();

    @Property("storage.expire-after")
    Duration getStorageExpireAfter();

    @Property("storage.batch-max-age")
    Duration getStorageBatchMaxAge();

    @Property("db.clean.older-than")
    Duration getDbCleanOlderThan();
}
