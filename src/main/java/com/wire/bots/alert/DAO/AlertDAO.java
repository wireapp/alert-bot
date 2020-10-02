package com.wire.bots.alert.DAO;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.UUID;

public interface AlertDAO {
    @SqlUpdate("INSERT INTO Alert (botId, conversationId) VALUES (:botId, :convId)")
    int insertSubscriber(@Bind("botId") UUID botId,
                         @Bind("convId") UUID convId);

    @SqlQuery("SELECT botId FROM Alert")
    @RegisterMapper(UUIDMapper.class)
    java.util.List<UUID> getSubscribers();

    @SqlUpdate("DELETE FROM Alert WHERE botId = :botId")
    int unsubscribe(@Bind("botId") UUID botId);
}
