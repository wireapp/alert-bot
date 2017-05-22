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
import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.Logger;
import com.wire.bots.sdk.WireClient;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class Broadcaster {
    private final ClientRepo repo;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(20);
    private final Config config;

    Broadcaster(ClientRepo repo, Config config) {
        this.repo = repo;
        this.config = config;
    }

    void broadcastText(final String messageId, final String text) throws SQLException {
        for (File botDir : getCryptoDirs()) {
            final String botId = botDir.getName();
            executor.execute(() -> sendText(messageId, text, botId));
        }
    }

    private void sendText(String messageId, String text, String botId) {
        try {
            WireClient client = repo.getWireClient(botId);
            client.sendText(text, 0, messageId);
        } catch (Exception e) {
            Logger.error("Bot: %s. Error: %s", botId, e.getMessage());
        }
    }

    private File[] getCryptoDirs() {
        File dir = new File(config.getCryptoDir());
        return dir.listFiles(file -> {
            String botId = file.getName();
            return repo.getWireClient(botId) != null;
        });
    }
}
