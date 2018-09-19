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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wire.bots.alert.model.Alert;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.user.UserClientRepo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

class Broadcaster {
    private final ClientRepo repo;
    private final Database db;

    Broadcaster(ClientRepo repo) {
        this.repo = repo;
        this.db = new Database(Service.config.postgres);
    }

    private WireClient getWireClient(String botId) throws Exception {
        return repo instanceof UserClientRepo
                ? ((UserClientRepo) repo).getWireClient(botId, db.getConversationId(botId))
                : repo.getWireClient(botId);
    }

    private ArrayList<String> getBots() throws Exception {
        return db.getSubscribers();
    }

    void broadcast(Alert payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String text = mapper.writeValueAsString(payload);
        for (String botId : getBots()) {
            try {
                boolean ret = filter(payload.getAnnotations(), db.getAnnotations(botId));
                if (ret) {
                    WireClient client = getWireClient(botId);
                    client.sendText(String.format("```\n%s\n```", text));
                }
            } catch (Exception e) {
                Logger.error("broadcastText: %s Error: %s", botId, e);
            }
        }
    }

    private boolean filter(Map<String, String> payloadAnnotations, Map<String, String> annotations) {
        if (payloadAnnotations == null)
            return !annotations.isEmpty();

        for (String key : payloadAnnotations.keySet()) {
            String value = annotations.get(key);
            if (value != null && !Objects.equals(value, payloadAnnotations.get(key))) {
                return false;
            }
        }

        for (String key : annotations.keySet()) {
            String value = payloadAnnotations.get(key);
            if (value != null && !Objects.equals(value, annotations.get(key))) {
                return false;
            }
        }
        return true;
    }
}
