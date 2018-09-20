package com.wire.bots.alert.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prometheus {
    @JsonProperty
    public String version;
    @JsonProperty
    public String externalURL;
    @JsonProperty
    public String status;
    @JsonProperty
    public String receiver;
    @JsonProperty
    public Map<String, String> groupLabels;
    @JsonProperty
    public Map<String, String> commonAnnotations;
    @JsonProperty
    public Map<String, String> commonLabels;
    @JsonProperty
    public ArrayList<Alert> alerts;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alert {
        @JsonProperty
        public String status;
        @JsonProperty
        public String startsAt;
        @JsonProperty
        public String endsAt;
        @JsonProperty
        public String generatorURL;
        @JsonProperty
        public Map<String, String> labels;
        @JsonProperty
        public Map<String, String> annotations;
    }
}
