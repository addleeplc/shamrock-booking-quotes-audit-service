/**
 * Copyright (c) 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.shamrock.booking.quotes.audit;

public class ErrorCode extends com.haulmont.monaco.response.ErrorCode {
//    public static ErrorCode TEST_ERROR = new ErrorCode(42, "THIS IS A TEST ERROR");

    public ErrorCode(int code, String message) {
        super(code, message);
    }
}