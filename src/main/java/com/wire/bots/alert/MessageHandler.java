package com.wire.bots.alert;

import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.tools.Logger;

import java.sql.SQLException;
import java.util.Map;

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

            String msg = String.format("`POST https://services.wire.com/alert/simple/%s`", client.getId());
            client.sendText(msg);
        } catch (Exception e) {
            Logger.error("onNewConversation: %s %s", client.getId(), e);
        }
    }

    @Override
    public void onBotRemoved(String botId) {
        try {
            db.unsubscribe(botId);
        } catch (SQLException e) {
            Logger.error("onBotRemoved: %s %s", botId, e);
        }
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            String botId = client.getId();
            String[] split = msg.getText().toLowerCase().trim().split(" ");
            String command = split[0];

            if (command.equals("/label") && split.length == 4) {
                String subCommand = split[1];
                String key = split[2];
                String value = split[3];

                if (subCommand.equals("add") && db.insertAnnotation(botId, key, value)) {
                    String text = String.format("Ok, filtering for `%s=%s`", key, value);
                    client.sendText(text);
                }

                if (subCommand.equals("remove") && db.removeAnnotation(botId, key, value)) {
                    String text = String.format("Ok, removed filtering for `%s=%s`", key, value);
                    client.sendText(text);
                }
            }

            if (command.equals("/labels")) {
                Map<String, String> annotations = db.getAnnotations(botId);
                StringBuilder sb = new StringBuilder();
                if (annotations.isEmpty())
                    sb.append("No labels");
                for (String k : annotations.keySet()) {
                    sb.append(k).append("=").append(annotations.get(k)).append("\n");
                }
                client.sendText("```\n" + sb.toString() + "```");
            }
        } catch (Exception e) {
            Logger.error("OnText: %s ex: %s", client.getId(), e);
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
