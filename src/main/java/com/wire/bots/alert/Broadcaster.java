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

import com.wire.bots.alert.DAO.AnnotationsDAO;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.exceptions.MissingStateException;
import com.wire.bots.sdk.tools.Logger;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Broadcaster {
    private final ClientRepo repo;
    private final Database db;

    public Broadcaster(DBI dbi, ClientRepo repo) {
        this.repo = repo;
        this.db = new Database(dbi);
    }

    private WireClient getClient(UUID botId) throws Exception {
        return repo.getClient(botId);
    }

    private List<UUID> getBots() {
        return db.getSubscribers();
    }

    public int broadcast(String text, Map<String, String> labels) {
        int count = 0;
        for (UUID botId : getBots()) {
            try (WireClient client = getClient(botId)) {
                if (filter(labels, db.getAnnotations(botId))) {
                    client.sendText(text);
                    count++;
                }
            } catch (MissingStateException e) {
                Logger.info("Bot previously deleted. Bot: %s", botId);
            } catch (Exception e) {
                Logger.error("broadcastText: %s Error: %s", botId, e);
            }
        }
        return count;
    }

    public int broadcast(String text) {
        int count = 0;
        for (UUID botId : db.getSubscribers()) {
            try (WireClient client = getClient(botId)) {
                client.sendText(text);
                count++;
            } catch (MissingStateException e) {
                Logger.info("Bot previously deleted. Bot: %s", botId);
            } catch (Exception e) {
                Logger.error("broadcastText: %s Error: %s", botId, e);
            }
        }
        return count;
    }

    public int call(Map<String, String> labels) {
        int count = 0;
        for (UUID botId : getBots()) {
            try (WireClient client = getClient(botId)) {
                if (filter(labels, db.getAnnotations(botId))) {
                    client.call("{\"version\":\"3.0\",\"type\":\"GROUPSTART\",\"sessid\":\"\",\"resp\":false}");
                    count++;
                }
            } catch (MissingStateException e) {
                Logger.info("Bot previously deleted. Bot: %s", botId);
            } catch (Exception e) {
                Logger.error("called: %s Error: %s", botId, e);
            }
        }
        return count;
    }

    private boolean filter(Map<String, String> first, List<AnnotationsDAO.Annotation> secondL) {
        if (secondL.isEmpty())
            return false;

        Map<String, AnnotationsDAO.Annotation> second = secondL.stream()
                .collect(Collectors.toMap(AnnotationsDAO.Annotation::getLabel, x -> x));

        for (String key : first.keySet()) {
            final AnnotationsDAO.Annotation s = second.get(key);
            if (s != null && !Objects.equals(s.value, first.get(key))) {
                return false;
            }
        }

        for (AnnotationsDAO.Annotation annotation : secondL) {
            final String value = first.get(annotation.label);
            if (value != null && !Objects.equals(value, annotation.value)) {
                return false;
            }
        }
        return true;
    }
}
