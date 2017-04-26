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
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.alert.model.Alert;
import com.wire.bots.alert.model.Config;
import com.wire.bots.alert.model.Payload;
import org.yaml.snakeyaml.Yaml;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/alert")
@Consumes(MediaType.APPLICATION_JSON)
public class BroadcastResource {
    private final Config conf;
    private final Broadcaster exec;
    private final static ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            return new Yaml();
        }
    };

    public BroadcastResource(ClientRepo repo, Config conf) {
        this.conf = conf;
        exec = new Broadcaster(repo, conf);
    }

    @POST
    @Path("{apiKey}")
    @Timed
    public Response broadcastAlert(@PathParam("apiKey") String apiKey,
                                   @NotNull @Valid Payload payload) throws Exception {

        if (!conf.getApiKeys().contains(apiKey))
            throw new ForbiddenException();

        for (Alert alert : payload.getAlerts()) {
            String txt = String.format(
                    "---\nStatus: %s\nAnnotations:\n%sLabels:\n%s\n",
                    alert.getStatus(),
                    yaml.get().dumpAsMap(alert.getAnnotations()),
                    yaml.get().dumpAsMap(alert.getLabels())
            );
            exec.broadcastText(UUID.randomUUID().toString(), txt);
        }

        return Response.
                accepted().
                build();
    }
}

