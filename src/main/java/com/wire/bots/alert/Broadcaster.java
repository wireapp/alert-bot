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
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.user.UserClientRepo;

import java.sql.SQLException;

class Broadcaster {
    private final UserClientRepo repo;
    private final Config config;

    Broadcaster(UserClientRepo repo, Config config) {
        this.repo = repo;
        this.config = config;
    }

    void broadcastText(final String messageId, final String text) throws SQLException {
        sendText(messageId, text, config.userId, config.convId);
    }

    private void sendText(String messageId, String text, String botId, String conv) {
        try {
            WireClient client = repo.getWireClient(botId, conv);
            client.sendText(text, 0, messageId);
        } catch (Exception e) {
            Logger.error("Bot: %s. Error: %s", botId, e.getMessage());
        }
    }
}
