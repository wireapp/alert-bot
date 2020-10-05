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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.wire.bots.alert.Broadcaster;
import com.wire.bots.alert.Service;
import com.wire.bots.alert.model.Prometheus;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.tools.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.skife.jdbi.v2.DBI;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

@Api
@Path("/prometheus")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrometheusResource {
    private final static MustacheFactory mf = new DefaultMustacheFactory();
    private static final String PROMETHEUS_MUSTACHE = "prometheus.mustache";
    private final Broadcaster broadcaster;
    private final ObjectMapper mapper = new ObjectMapper();

    public PrometheusResource(DBI dbi, ClientRepo repo) {

        broadcaster = new Broadcaster(dbi, repo);
    }

    @POST
    @Timed
    @ApiOperation(value = "Broadcast prometheus object on Wire")
    public Response webhook(@ApiParam("Bearer token") @NotNull @Valid @HeaderParam("Authorization") String token,
                            @ApiParam String payload) {

        try {
            Logger.debug(payload);

            final Prometheus prometheus = mapper.readValue(payload, Prometheus.class);
            String challenge = String.format("Bearer %s", Service.instance.getConfig().prometheusToken);
            if (!Objects.equals(token, challenge)) {
                Logger.warning("PrometheusResource: Wrong Authorization: %s from %s", token, prometheus.externalURL);
                return Response.
                        status(401).
                        build();
            }

            String icon = isFiring(prometheus.status)
                    ? "\uD83D\uDD25"
                    : "\uD83D\uDC4C";
            prometheus.commonAnnotations.put("icon", icon);

            Mustache template = getTemplate();
            String text = execute(template, prometheus);
            int broadcast = broadcaster.broadcast(text, prometheus.commonLabels);

            Logger.info("PrometheusResource: New payload from %s texted in %d convs", prometheus.externalURL, broadcast);

            if (isCritical(prometheus.commonLabels) && isFiring(prometheus.status) && isProd(prometheus.commonLabels)) {
                int call = broadcaster.call(prometheus.commonLabels);
                Logger.info("PrometheusResource: New payload from %s called in %d convs", prometheus.externalURL, call);
            }

            return Response.
                    accepted().
                    build();
        } catch (Exception e) {
            Logger.error("PrometheusResource: %s", e);
            e.printStackTrace();
            return Response.
                    ok().
                    status(500).
                    build();
        }
    }

    private boolean isFiring(String status) {
        return Objects.equals(status, "firing");
    }

    private boolean isCritical(Map<String, String> commonLabels) {
        return Objects.equals(commonLabels.get("severity"), "critical");
    }

    private boolean isProd(Map<String, String> commonLabels) {
        return Objects.equals(commonLabels.get("env"), "prod");
    }

    private Mustache getTemplate() {
        String path = String.format("templates/%s", PROMETHEUS_MUSTACHE);
        return mf.compile(path);
    }

    private String execute(Mustache mustache, Prometheus model) throws IOException {
        try (StringWriter sw = new StringWriter()) {
            mustache.execute(new PrintWriter(sw), model).flush();
            return sw.toString();
        }
    }
}

