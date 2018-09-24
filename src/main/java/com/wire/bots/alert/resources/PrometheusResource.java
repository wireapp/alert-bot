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
package com.wire.bots.alert.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wire.bots.alert.Broadcaster;
import com.wire.bots.alert.Service;
import com.wire.bots.alert.model.Prometheus;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.tools.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/prometheus")
@Consumes(MediaType.APPLICATION_JSON)
public class PrometheusResource {
    private final Broadcaster broadcaster;
    private final ObjectMapper mapper;

    public PrometheusResource(ClientRepo repo) {
        broadcaster = new Broadcaster(repo);
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @POST
    @Timed
    public Response webhook(@NotNull @Valid @HeaderParam("Authorization") String token,
                            @NotNull @Valid Prometheus payload) throws Exception {

        Logger.info("New payload: %s", payload.externalURL);

        if (!Objects.equals(token, String.format("Bearer %s", Service.config.getPrometheusToken())))
            return Response.
                    status(401).
                    build();

        for (Prometheus.Alert alert : payload.alerts) {
            String text = mapper.writeValueAsString(alert);
            broadcaster.broadcast(text, alert.labels);
        }

        return Response.
                accepted().
                build();
    }
}

