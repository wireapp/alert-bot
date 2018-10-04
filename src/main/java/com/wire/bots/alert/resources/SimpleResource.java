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
import com.wire.bots.alert.Broadcaster;
import com.wire.bots.alert.Service;
import com.wire.bots.alert.model.Simple;
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
import java.util.HashMap;
import java.util.Objects;

@Path("/simple")
@Consumes(MediaType.APPLICATION_JSON)
public class SimpleResource {
    private final Broadcaster broadcaster;

    public SimpleResource(ClientRepo repo) {
        broadcaster = new Broadcaster(repo);
    }

    @POST
    @Timed
    public Response webhook(@NotNull @Valid @HeaderParam("Authorization") String token,
                            @NotNull @Valid Simple payload) {

        try {
            String challenge = String.format("Bearer %s", Service.config.getPrometheusToken());
            if (!Objects.equals(token, challenge)) {
                Logger.warning("SimpleResource: Wrong Authorization: %s", token);
                return Response.
                        status(401).
                        build();
            }

            int broadcast = broadcaster.broadcast(payload.message, new HashMap<>());

            Logger.info("SimpleResource: New payload texted in %d convs", broadcast);

            return Response.
                    accepted().
                    build();
        } catch (Exception e) {
            Logger.error("SimpleResource: %s", e);
            return Response.
                    serverError().
                    build();
        }
    }
}

