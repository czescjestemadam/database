package dev.czescjestemadam.database.query.impl;

import dev.czescjestemadam.database.query.AbstractPreparedQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SimpleQuery extends AbstractPreparedQuery<Boolean> {
	public SimpleQuery(String sql, List<Object> parameters) {
		super(sql, parameters, false);
	}

	@Override
	public Boolean execute(Connection connection) throws SQLException {
		return prepareStatement(connection).execute();
	}

	@Override
	public String toString() {
		return "SimpleQuery{" +
			"sql='" + sql + '\'' +
			", parameters=" + parameters +
			", returnGeneratedKeys=" + returnGeneratedKeys +
			'}';
	}
}
