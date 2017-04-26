package com.wire.bots.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * An approximation of the prometheus alertmanager webhook receiver payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Payload {

    private final List<Alert> alerts;

    @JsonCreator
    public Payload(@JsonProperty("alerts") List<Alert> alerts) {
        this.alerts = alerts;
    }

    @JsonProperty
    public List<Alert> getAlerts() {
        return alerts;
    }
}

