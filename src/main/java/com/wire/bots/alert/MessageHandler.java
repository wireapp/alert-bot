package com.wire.bots.alert;

import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.tools.Logger;

import java.sql.SQLException;

public class MessageHandler extends MessageHandlerBase {
    private final Config config;
    private final Database db;

    MessageHandler(Config config) {
        this.config = config;
        db = new Database(Service.config.getPostgres());
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
            if (db.insertSubscriber(client.getId(), client.getConversationId()))
                Logger.info("onNewConversation. New subscriber, %s", client.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onBotRemoved(String botId) {
        try {
            db.unsubscribe(botId);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            String botId = client.getId();
            String[] split = msg.getText().toLowerCase().trim().split(" ");
            if (split.length < 4)
                return;

            String command = split[0];
            String subCommand = split[1];
            String key = split[2];
            String value = split[3];

            if (!command.equals("/annotation")) {
                return;
            }

            if (subCommand.equals("add") && db.insertAnnotation(botId, key, value)) {
                String text = String.format("Ok, filtering for `%s=%s`", key, value);
                client.sendText(text);
            }

            if (subCommand.equals("remove") && db.removeAnnotation(botId, key, value)) {
                String text = String.format("Ok, removed filtering for `%s=%s`", key, value);
                client.sendText(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

}
