package com.wire.bots.alert.DAO;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.Map;
import java.util.UUID;

public interface AnnotationsDAO {
    @SqlUpdate("INSERT INTO Annotations (botId, key, value) VALUES (:botId, :key, :value)")
    int insert(@Bind("botId") UUID botId,
               @Bind("key") String key,
               @Bind("value") String value);

    @SqlQuery("SELECT key, value FROM Annotations WHERE botId = :botId")
    Map<String, String> get(@Bind("botId") UUID botId);

    @SqlUpdate("DELETE FROM Annotations WHERE botId = :botId AND key = :key AND value = :value")
    int remove(@Bind("botId") UUID botId,
               @Bind("key") String key,
               @Bind("value") String value);
}
