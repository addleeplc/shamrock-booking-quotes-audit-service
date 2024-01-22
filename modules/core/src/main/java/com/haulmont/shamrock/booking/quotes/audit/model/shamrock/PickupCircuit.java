/*
 * Copyright 2008 - 2023 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.booking.quotes.audit.model.shamrock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.haulmont.bali.jackson.joda.DateTimeAdapter;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PickupCircuit {

    @JsonProperty("id")
    private String id;

    @JsonProperty("pid")
    private String pid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("locked")
    private Boolean locked;

    @JsonProperty("locked_max_Priority")
    private Integer lockedMaxPriority;

    @JsonProperty("locked_till")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    private DateTime lockedTill;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Integer getLockedMaxPriority() {
        return lockedMaxPriority;
    }

    public void setLockedMaxPriority(Integer lockedMaxPriority) {
        this.lockedMaxPriority = lockedMaxPriority;
    }

    public DateTime getLockedTill() {
        return lockedTill;
    }

    public void setLockedTill(DateTime lockedTill) {
        this.lockedTill = lockedTill;
    }
}

