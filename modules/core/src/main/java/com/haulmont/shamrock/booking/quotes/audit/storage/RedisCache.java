/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.storage;

import com.haulmont.monaco.AppContext;
import com.haulmont.monaco.redis.Redis;
import com.haulmont.monaco.redis.cache.RedisCacheObjectCodec;
import com.haulmont.monaco.redis.cache.codec.JacksonObjectListCodec;
import com.haulmont.monaco.redis.cache.codec.PropertyObjectCodec;
import com.haulmont.shamrock.booking.quotes.audit.ServiceConfiguration;
import com.haulmont.shamrock.booking.quotes.audit.dto.ProductQuotationRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RedisCache implements ProductQuotationRecordStorage {

    private final PropertyObjectCodec<UUID> keyCodec = new PropertyObjectCodec<>("BookingQuotesAudit", UUID.class, "bookingStatus");
    private final RedisCacheObjectCodec<List<ProductQuotationRecord>> valueCodec = new JacksonObjectListCodec<>(ProductQuotationRecord.class);

    private final ServiceConfiguration configuration;

    private final String redisResources;

    public RedisCache(ServiceConfiguration configuration, String redisResources) {
        this.configuration = configuration;
        this.redisResources = redisResources;
    }

    @Override
    public List<ProductQuotationRecord> get(UUID bookingId) {
        Redis<String, String> redis = getRedis();

        String key = keyCodec.encode(bookingId);
        List<String> rawValues = redis.lrange(key, 0L, -1L);

        List<ProductQuotationRecord> result = new ArrayList<>();
        for (String rawValue : rawValues) {
            result.addAll(valueCodec.decode(rawValue));
        }

        return result;
    }

    @Override
    public void put(UUID bookingId, ProductQuotationRecord record) {
        Redis<String, String> redis = getRedis();

        String key = keyCodec.encode(bookingId);
        String strValue = valueCodec.encode(Collections.singletonList(record));
        redis.lpush(key, strValue);
        redis.expire(key, configuration.getStorageExpireAfterMinutes() * 60);
    }

    @Override
    public void remove(UUID bookingId) {
        Redis<String, String> redis = getRedis();
        String key = keyCodec.encode(bookingId);
        redis.del(key);
    }

    @Override
    public List<UUID> keys() {
        Redis<String, String> redis = getRedis();

        List<UUID> ids = new ArrayList<>();

        for (String key : redis.keys(keyCodec.getKeyPattern())) {
            UUID bookingId = keyCodec.decode(key);
            ids.add(bookingId);
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    private Redis<String, String> getRedis() {
        return AppContext.getResources().get(redisResources, Redis.class);
    }
}
