package com.haulmont.shamrock.booking.quotes.audit.model.shamrock;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class PrebookLimit {
    @JsonProperty("id")
    private String id;

    @JsonProperty("circuit_id")
    private String circuitId;

    @JsonProperty("reserve_id")
    private String reserveId;

    @JsonProperty("availability_type")
    private AvailabilityType availabilityType;

    @JsonProperty("gap_start")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private DateTime gapStart;
    @JsonProperty("gap_end")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private DateTime gapEnd;

    @JsonProperty("limit_cash")
    private long limitCash;
    @JsonProperty("limit_cash_extended")
    private long limitCashExtended;
    @JsonProperty("limit_account")
    private long limitAccount;

    @JsonProperty("current_cash")
    private long currentCash;
    @JsonProperty("current_account")
    private long currentAccount;
    @JsonProperty("reserved_cash")
    private long reservedCash;
    @JsonProperty("reserved_account")
    private long reservedAccount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(String circuitId) {
        this.circuitId = circuitId;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public AvailabilityType getAvailabilityType() {
        return availabilityType;
    }

    public DateTime getGapStart() {
        return gapStart;
    }

    public void setGapStart(DateTime gapStart) {
        this.gapStart = gapStart;
    }

    public DateTime getGapEnd() {
        return gapEnd;
    }

    public void setGapEnd(DateTime gapEnd) {
        this.gapEnd = gapEnd;
    }

    public long getLimitCash() {
        return limitCash;
    }

    public void setLimitCash(long limitCash) {
        this.limitCash = limitCash;
    }

    public long getLimitCashExtended() {
        return limitCashExtended;
    }

    public void setLimitCashExtended(long limitCashExtended) {
        this.limitCashExtended = limitCashExtended;
    }

    public long getLimitAccount() {
        return limitAccount;
    }

    public void setLimitAccount(long limitAccount) {
        this.limitAccount = limitAccount;
    }

    public long getCurrentCash() {
        return currentCash;
    }

    public void setCurrentCash(long currentCash) {
        this.currentCash = currentCash;
    }

    public long getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(long currentAccount) {
        this.currentAccount = currentAccount;
    }

    public long getReservedCash() {
        return reservedCash;
    }

    public void setReservedCash(long reservedCash) {
        this.reservedCash = reservedCash;
    }

    public long getReservedAccount() {
        return reservedAccount;
    }

    public void setReservedAccount(long reservedAccount) {
        this.reservedAccount = reservedAccount;
    }

    public void setAvailabilityType(AvailabilityType availabilityType) {
        this.availabilityType = availabilityType;
    }

    public enum AvailabilityType {
        AVAILABLE("AVAILABLE"),
        NOT_AVAILABLE("NOT_AVAILABLE"),
        NOT_AVAILABLE_GROUP("NOT_AVAILABLE_GROUP"),
        NOT_WORK("NOT_WORK"),
        REGION_NOT_FOUND("REGION_NOT_FOUND"),
        SERVICE_GROUP_NOT_FOUND("SERVICE_GROUP_NOT_FOUND"),
        CIRCUIT_NOT_FOUND("CIRCUIT_NOT_FOUND"),
        CURRENT_LIMIT_NOT_FOUND("CURRENT_LIMIT_NOT_FOUND"),
        LIMITS_NOT_FOUND("LIMITS_NOT_FOUND"),
        ERROR("ERROR");

        private String code;

        AvailabilityType(String code) {
            this.code = code;
        }

        private static Map<String, AvailabilityType> map = new HashMap<>();

        static {
            for (AvailabilityType type : values()) {
                map.put(type.getCode(), type);
            }
        }

        public String getCode() {
            return code;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.code;
        }

        @JsonCreator
        public static AvailabilityType fromValue(String code) {
            AvailabilityType constant = map.get(code);
            if (constant == null) {
                throw new IllegalArgumentException(code);
            } else {
                return constant;
            }
        }
    }
}
