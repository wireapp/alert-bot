package com.wire.bots.alert.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Approximation of an alert as sent by the prometheus alertmanager (webhook
 * receiver).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Alert {
    private final String status;
    private final Map<String, String> labels;
    private final Map<String, String> annotations;

    @JsonCreator
    public Alert(@JsonProperty("status") String status,
                 @JsonProperty("labels") Map<String, String> labels,
                 @JsonProperty("annotations") Map<String, String> annotations) {
        this.status = status;
        this.labels = labels;
        this.annotations = annotations;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    @JsonProperty
    public Map<String, String> getLabels() {
        return labels;
    }

    @JsonProperty
    public Map<String, String> getAnnotations() {
        return annotations;
    }
}
