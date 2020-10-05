package com.wire.bots.alert;

import com.wire.bots.alert.DAO.AnnotationsDAO;
import com.wire.bots.alert.model.Config;
import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.models.TextMessage;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.server.model.SystemMessage;
import com.wire.bots.sdk.tools.Logger;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.UUID;

public class MessageHandler extends MessageHandlerBase {
    private final Database db;

    MessageHandler(DBI dbi) {
        db = new Database(dbi);
    }

    @Override
    public boolean onNewBot(NewBot newBot, String serviceToken) {
        Logger.info(String.format("onNewBot: bot: %s, user: %s",
                newBot.id,
                newBot.origin.id));

        final Config config = Service.instance.getConfig();
        return !checkWhitelist(newBot.origin.handle, config.whitelist);
    }

    @Override
    public void onNewConversation(WireClient client, SystemMessage message) {
        UUID botId = client.getId();
        UUID convId = client.getConversationId();

        try {
            if (1 == db.insertSubscriber(botId, convId))
                Logger.info("onNewConversation. New subscriber, %s", botId);

            String msg = String.format("`POST https://services.wire.com/alert/simple/%s`", botId);
            client.sendText(msg);
        } catch (Exception e) {
            Logger.error("onNewConversation: %s %s", botId, e);
        }
    }

    @Override
    public void onBotRemoved(UUID botId, SystemMessage msg) {
        if (1 == db.unsubscribe(botId)) {
            Logger.info("onBotRemoved: %s %s", botId);
        }
    }

    @Override
    public void onText(WireClient client, TextMessage msg) {
        try {
            UUID botId = client.getId();
            String[] split = msg.getText().toLowerCase().trim().split(" ");
            String command = split[0];

            if (command.equals("/label") && split.length == 4) {
                String subCommand = split[1];
                String key = split[2];
                String value = split[3];

                if (subCommand.equals("add") && 1 == db.insertAnnotation(botId, key, value)) {
                    String text = String.format("Ok, filtering for `%s=%s`", key, value);
                    client.sendText(text);
                }

                if (subCommand.equals("remove") && 1 == db.removeAnnotation(botId, key, value)) {
                    String text = String.format("Ok, removed filtering for `%s=%s`", key, value);
                    client.sendText(text);
                }
            }

            if (command.equals("/labels")) {
                final List<AnnotationsDAO.Annotation> annotations = db.getAnnotations(botId);
                StringBuilder sb = new StringBuilder();
                if (annotations.isEmpty())
                    sb.append("No labels");
                for (AnnotationsDAO.Annotation annotation : annotations) {
                    sb.append(annotation.label).append("=").append(annotation.value).append("\n");
                }
                client.sendText("```\n" + sb.toString() + "```");
            }
        } catch (Exception e) {
            Logger.error("OnText: %s ex: %s", client.getId(), e);
        }
    }

    private boolean checkWhitelist(String handle, String whitelist) {
        if (whitelist != null && !whitelist.isEmpty()) {
            return !isWhitelisted(handle, whitelist);
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
