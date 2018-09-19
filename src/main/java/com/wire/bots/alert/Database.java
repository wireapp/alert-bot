package com.wire.bots.alert;

import com.wire.bots.sdk.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

class Database {
    private final Configuration.DB conf;

    Database(Configuration.DB conf) {
        this.conf = conf;
    }

    boolean insertSubscriber(String botId, String convId) throws Exception {
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Alert (botId, conversationId) VALUES (?, ?) " +
                    "ON CONFLICT (botId) DO NOTHING");
            stmt.setObject(1, UUID.fromString(botId));
            stmt.setObject(2, UUID.fromString(convId));
            return stmt.executeUpdate() == 1;
        }
    }

    ArrayList<String> getSubscribers() throws Exception {
        ArrayList<String> ret = new ArrayList<>();
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT botId FROM Alert");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                ret.add(resultSet.getString("botId"));
            }
        }
        return ret;
    }

    String getConversationId(String botId) throws Exception {
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT conversationId FROM Alert WHERE botId = ?");
            stmt.setObject(1, UUID.fromString(botId));
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("conversationId");
            }
        }
        return null;
    }

    private Connection newConnection() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%d/%s", conf.host, conf.port, conf.database);
        return DriverManager.getConnection(url, conf.user, conf.password);
    }
}
