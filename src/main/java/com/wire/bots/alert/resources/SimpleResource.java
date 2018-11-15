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
import com.wire.bots.alert.model.Simple;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.exceptions.MissingStateException;
import com.wire.bots.sdk.tools.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/simple/{botId}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimpleResource {
    private final ClientRepo repo;

    public SimpleResource(ClientRepo repo) {
        this.repo = repo;
    }

    @POST
    @Timed
    public Response webhook(@PathParam("botId") String botId, @NotNull @Valid Simple payload) {
        try {
            WireClient client = repo.getClient(botId);
            client.sendText(payload.message);

            Logger.info("%s SimpleResource: New payload texted", botId);

            return Response.
                    accepted().
                    build();
        } catch (MissingStateException e) {
            Logger.info("%s SimpleResource: %s", botId, e);
            return Response.
                    status(404).
                    build();
        } catch (Exception e) {
            Logger.error("%s SimpleResource: %s", botId, e);
            return Response.
                    serverError().
                    build();
        }
    }
}

