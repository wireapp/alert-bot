//
// Wire
// Copyright (C) 2016 Wire Swiss GmbH
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.
//

package com.wire.bots.alert.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wire.bots.sdk.Configuration;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config extends Configuration {
    @JsonProperty
    public String whitelist;

    @JsonProperty
    @NotNull
    public String prometheusToken;

    @JsonProperty
    @NotNull
    public DB postgres = new DB();

    @JsonProperty
    @NotNull
    public UUID serviceId;

    public DB getPostgres() {
        return postgres;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public String getPrometheusToken() {
        return prometheusToken;
    }

    public UUID getServiceId() {
        return serviceId;
    }
}
