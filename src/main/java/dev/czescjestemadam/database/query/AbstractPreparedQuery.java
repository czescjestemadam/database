package dev.czescjestemadam.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractPreparedQuery<T> implements Query<T> {
	protected final String sql;
	protected final List<Object> parameters;
	protected final boolean returnGeneratedKeys;

	protected AbstractPreparedQuery(String sql, List<Object> parameters, boolean returnGeneratedKeys) {
		this.sql = sql;
		this.parameters = parameters;
		this.returnGeneratedKeys = returnGeneratedKeys;
	}

	public String getSql() {
		return sql;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public boolean isReturnGeneratedKeys() {
		return returnGeneratedKeys;
	}

	protected PreparedStatement prepareStatement(Connection connection) throws SQLException {
		final PreparedStatement statement = returnGeneratedKeys ?
				connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS) :
				connection.prepareStatement(sql);

		for (int i = 0; i < parameters.size(); i++) {
			statement.setObject(i + 1, parameters.get(i));
		}

		return statement;
	}
}
