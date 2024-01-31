/**
 * Copyright (c) 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.dto;

import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;

import java.util.List;
import java.util.UUID;

public class KeysResponse extends Response {

    private List<UUID> keys;

    public KeysResponse(List<UUID> keys, ErrorCode code) {
        super(code);
        this.keys = keys;
    }

    public List<UUID> getKeys() {
        return keys;
    }

    public void setKeys(List<UUID> keys) {
        this.keys = keys;
    }
}
