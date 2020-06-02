package com.wire.bots.alert;

import com.wire.bots.alert.DAO.AlertDAO;
import com.wire.bots.alert.DAO.AnnotationsDAO;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class Database {
    private final AlertDAO alertDAO;
    private final AnnotationsDAO annotationsDAO;

    Database(DBI dbi) {

        alertDAO = dbi.onDemand(AlertDAO.class);
        annotationsDAO = dbi.onDemand(AnnotationsDAO.class);
    }

    int insertSubscriber(UUID botId, UUID convId) {
        return alertDAO.insertSubscriber(botId, convId);
    }

    List<UUID> getSubscribers() {
        return alertDAO.getSubscribers();
    }

    int unsubscribe(UUID botId) {
        return alertDAO.unsubscribe(botId);
    }

    int insertAnnotation(UUID botId, String key, String value) {
        return annotationsDAO.insert(botId, key, value);
    }

    Map<String, String> getAnnotations(UUID botId) {
        return annotationsDAO.get(botId);
    }

    int removeAnnotation(UUID botId, String key, String value) {
        return annotationsDAO.remove(botId, key, value);
    }
}
