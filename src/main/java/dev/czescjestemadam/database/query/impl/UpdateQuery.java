package dev.czescjestemadam.database.query.impl;

import dev.czescjestemadam.database.exceptions.query.QueryException;
import dev.czescjestemadam.database.query.AbstractPreparedQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UpdateQuery extends AbstractPreparedQuery<Integer> {
	private PreparedStatement preparedStatement;

	public UpdateQuery(String sql, List<Object> parameters, boolean returnGeneratedKeys) {
		super(sql, parameters, returnGeneratedKeys);
	}

	@Override
	public Integer execute(Connection connection) throws SQLException {
		preparedStatement = prepareStatement(connection);
		return preparedStatement.executeUpdate();
	}

	public ResultSet getGeneratedKeys() {
		if (!returnGeneratedKeys) {
			throw new QueryException("Query has returnGeneratedKeys set to false");
		}

		try {
			return preparedStatement.getGeneratedKeys();
		} catch (final SQLException e) {
			throw new QueryException("Error getting generated keys", e);
		}
	}

	@Override
	public String toString() {
		return "UpdateQuery{" +
			"preparedStatement=" + preparedStatement +
			", sql='" + sql + '\'' +
			", parameters=" + parameters +
			", returnGeneratedKeys=" + returnGeneratedKeys +
			'}';
	}
}
