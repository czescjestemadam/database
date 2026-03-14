package dev.czescjestemadam.database.query.impl;

import dev.czescjestemadam.database.query.AbstractPreparedQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SelectQuery extends AbstractPreparedQuery<ResultSet> {
	public SelectQuery(String sql, List<Object> parameters) {
		super(sql, parameters, false);
	}

	@Override
	public ResultSet execute(Connection connection) throws SQLException {
		return prepareStatement(connection).executeQuery();
	}

	@Override
	public String toString() {
		return "SelectQuery{" +
			"sql='" + sql + '\'' +
			", parameters=" + parameters +
			", returnGeneratedKeys=" + returnGeneratedKeys +
			'}';
	}
}
