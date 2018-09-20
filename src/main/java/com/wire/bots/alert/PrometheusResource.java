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
package com.wire.bots.alert;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wire.bots.alert.model.Alert;
import com.wire.bots.sdk.ClientRepo;

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

    PrometheusResource(ClientRepo repo) {
        broadcaster = new Broadcaster(repo);
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @POST
    @Timed
    public Response webhook(@NotNull @Valid @HeaderParam("bearer_token") String secret,
                            @NotNull @Valid Alert payload) throws Exception {
        if (!Objects.equals(secret, Service.config.getSecret()))
            return Response.
                    status(403).
                    build();

        String text = mapper.writeValueAsString(payload);

        broadcaster.broadcast(text, payload.getAnnotations());

        return Response.
                accepted().
                build();
    }
}

