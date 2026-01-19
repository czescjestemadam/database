package dev.czescjestemadam.database.query;

import java.sql.Connection;
import java.sql.SQLException;

public interface Query<T> {
	T execute(Connection connection) throws SQLException;
}
