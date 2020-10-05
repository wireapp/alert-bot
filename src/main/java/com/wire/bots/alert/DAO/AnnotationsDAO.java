package com.wire.bots.alert.DAO;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface AnnotationsDAO {
    @SqlUpdate("INSERT INTO Annotations (botId, key, value) VALUES (:botId, :key, :value)")
    int insert(@Bind("botId") UUID botId,
               @Bind("key") String key,
               @Bind("value") String value);

    @SqlQuery("SELECT key, value FROM Annotations WHERE botId = :botId")
    @RegisterMapper(AnnotationMapper.class)
    List<Annotation> get(@Bind("botId") UUID botId);

    @SqlUpdate("DELETE FROM Annotations WHERE botId = :botId AND key = :key AND value = :value")
    int remove(@Bind("botId") UUID botId,
               @Bind("key") String key,
               @Bind("value") String value);

    class Annotation {
        public String label;
        public String value;

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }

    class AnnotationMapper implements ResultSetMapper<Annotation> {
        @Override
        public Annotation map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
            Annotation ret = new Annotation();
            ret.label = rs.getString("key");
            ret.value = rs.getString("value");
            return ret;
        }
    }
}
