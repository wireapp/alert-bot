package com.wire.bots.alert.DAO;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

class UUIDMapper implements ResultSetMapper<UUID> {
    @Override
    public UUID map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return (UUID) rs.getObject("botId");
    }
}
