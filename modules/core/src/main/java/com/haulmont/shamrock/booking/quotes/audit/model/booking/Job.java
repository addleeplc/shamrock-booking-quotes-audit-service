/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.booking;

import java.util.HashMap;
import java.util.Map;

public class Job {
    public enum ExecutionStatus {
        UNKNOWN(-1),
        BOOKED(0),
        WAITING_ALLOC(10),
        ALLOCATED(20),
        CONFIRMED(21),
        ON_WAY(30),
        AT_PICKUP(40),
        ON_BOARD(50),
        DONE(60),
        CANCELLED(70),
        ON_HOLD(-70);

        private static Map<Integer, ExecutionStatus> executionStatusByCode = new HashMap<Integer, ExecutionStatus>();
        static {
            for (ExecutionStatus executionStatus : ExecutionStatus.values()) {
                executionStatusByCode.put(executionStatus.getCode(), executionStatus);
            }
        }

        private Integer code;
        public Integer getCode() {
            return code;
        }

        ExecutionStatus(Integer code) {
            this.code = code;
        }

        public static ExecutionStatus valueOf(Integer code) {
            if (code == null) {
                return null;
            }
            ExecutionStatus executionStatus = executionStatusByCode.get(code);
            if (executionStatus == null) {
                executionStatus = UNKNOWN;
            }
            return executionStatus;
        }
    }
}
