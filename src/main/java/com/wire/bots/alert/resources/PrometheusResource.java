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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
public class PrometheusResource {
    private final static MustacheFactory mf = new DefaultMustacheFactory();
    private static final String PROMETHEUS_MUSTACHE = "prometheus.mustache";
    private final Broadcaster broadcaster;

    public PrometheusResource(ClientRepo repo) {
        broadcaster = new Broadcaster(repo);
    }

    private static String execute(Mustache mustache, Object model) throws IOException {
        try (StringWriter sw = new StringWriter()) {
            mustache.execute(new PrintWriter(sw), model).flush();
            return sw.toString();
        }
    }

    @POST
    @Timed
    @ApiOperation(value = "Broadcast prometheus object on Wire")
    public Response webhook(@ApiParam("Bearer token") @NotNull @Valid @HeaderParam("Authorization") String token,
                            @ApiParam @NotNull @Valid Prometheus payload) {

        try {
            String challenge = String.format("Bearer %s", Service.config.getPrometheusToken());
            if (!Objects.equals(token, challenge)) {
                Logger.warning("PrometheusResource: Wrong Authorization: %s from %s", token, payload.externalURL);
                return Response.
                        status(401).
                        build();
            }

            String icon = isFiring(payload.status)
                    ? "\uD83D\uDD25"
                    : "\uD83D\uDC4C";
            payload.commonAnnotations.put("icon", icon);

            Mustache template = getTemplate();
            String text = execute(template, payload);
            int broadcast = broadcaster.broadcast(text, payload.commonLabels);

            Logger.info("PrometheusResource: New payload from %s texted in %d convs", payload.externalURL, broadcast);

            if (isCritical(payload.commonLabels) && isFiring(payload.status) && isProd(payload.commonLabels)) {
                int call = broadcaster.call(payload.commonLabels);
                Logger.info("PrometheusResource: New payload from %s called in %d convs", payload.externalURL, call);
            }

            return Response.
                    accepted().
                    build();
        } catch (Exception e) {
            Logger.error("PrometheusResource: %s %s", payload.externalURL, e);
            return Response.
                    serverError().
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
}

