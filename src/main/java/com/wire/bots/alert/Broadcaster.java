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

import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.exceptions.MissingStateException;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.user.UserClientRepo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Broadcaster {
    private final ClientRepo repo;
    private final Database db;

    public Broadcaster(ClientRepo repo) {
        this.repo = repo;
        this.db = new Database(Service.config.postgres);
    }

    private WireClient getClient(String botId) throws Exception {
        return repo instanceof UserClientRepo
                ? ((UserClientRepo) repo).getWireClient(botId, db.getConversationId(botId))
                : repo.getClient(botId);
    }

    private ArrayList<String> getBots() throws Exception {
        return db.getSubscribers();
    }

    public int broadcast(String text, Map<String, String> labels) throws Exception {
        int count = 0;
        for (String botId : getBots()) {
            try {
                if (filter(labels, db.getAnnotations(botId))) {
                    WireClient client = getClient(botId);
                    client.sendText(text);
                    count++;
                }
            } catch (MissingStateException e) {
                Logger.warning("Bot previously deleted. Bot: %s", botId);
            } catch (Exception e) {
                Logger.error("broadcastText: %s Error: %s", botId, e);
            }
        }
        return count;
    }

    public int call(Map<String, String> labels) throws Exception {
        int count = 0;
        for (String botId : getBots()) {
            try {
                if (filter(labels, db.getAnnotations(botId))) {
                    WireClient client = getClient(botId);
                    client.call("{\"version\":\"3.0\",\"type\":\"GROUPSTART\",\"sessid\":\"\",\"resp\":false}");
                    count++;
                }
            } catch (MissingStateException e) {
                Logger.warning("Bot previously deleted. Bot: %s", botId);
            } catch (Exception e) {
                Logger.error("called: %s Error: %s", botId, e);
            }
        }
        return count;
    }

    private boolean filter(Map<String, String> first, Map<String, String> second) {
        for (String key : first.keySet()) {
            String value = second.get(key);
            if (value != null && !Objects.equals(value, first.get(key))) {
                return false;
            }
        }

        for (String key : second.keySet()) {
            String value = first.get(key);
            if (value != null && !Objects.equals(value, second.get(key))) {
                return false;
            }
        }
        return true;
    }
}
