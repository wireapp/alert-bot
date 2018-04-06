package com.wire.bots.alert;

import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.server.model.NewBot;

public class MessageHandler extends MessageHandlerBase {
    private final Config config;

    MessageHandler(Config config) {
        this.config = config;
    }

    @Override
    public boolean onNewBot(NewBot newBot) {
        return !checkWhitelist(newBot.origin.handle, config.whitelist);
    }

    private boolean checkWhitelist(String handle, String whitelist) {
        if (whitelist != null && !whitelist.isEmpty()) {
            if (!isWhitelisted(handle, whitelist))
                return true;
        }
        return false;
    }

    private boolean isWhitelisted(String handle, String whitelist) {
        for (String white : whitelist.split(",")) {
            if (white.trim().equalsIgnoreCase(handle)) {
                return true;
            }
        }
        return false;
    }
}
