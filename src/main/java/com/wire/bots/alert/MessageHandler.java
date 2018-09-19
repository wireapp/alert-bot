package com.wire.bots.alert;

import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.tools.Logger;

public class MessageHandler extends MessageHandlerBase {
    private final Config config;

    MessageHandler(Config config) {
        this.config = config;
    }

    @Override
    public boolean onNewBot(NewBot newBot) {
        Logger.info(String.format("onNewBot: bot: %s, user: %s",
                newBot.id,
                newBot.origin.id));
        return !checkWhitelist(newBot.origin.handle, config.getWhitelist());
    }

    @Override
    public void onNewConversation(WireClient client) {
        try {
            boolean insertSubscriber = getDatabase().insertSubscriber(client.getId(), client.getConversationId());
            if (insertSubscriber)
                client.sendText("Hey");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    private boolean checkWhitelist(String handle, String whitelist) {
        if (whitelist != null && !whitelist.isEmpty()) {
            if (!isWhitelisted(handle, whitelist))
                return true;
        }
        return false;
    }

    private boolean isWhitelisted(String handle, String whitelist) {
        String replace = whitelist.replace("@", "");
        for (String white : replace.split(",")) {
            if (white.trim().equalsIgnoreCase(handle)) {
                return true;
            }
        }
        return false;
    }

    private Database getDatabase() {
        return new Database(Service.config.getPostgres());
    }

}
