package dev.czescjestemadam.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.utils.ExceptionMapper;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
	private final HikariConfig config;
	private final SqlDialect sqlDialect;
	private final DataSource dataSource;

	public DatabaseConnectionManager(HikariConfig config) {
		this.config = config;
		this.sqlDialect = SqlDialect.detect(config.getJdbcUrl());
		this.dataSource = new HikariDataSource(config);
	}

	public <R> R connected(ConnectedFunction<R> func) {
		try (final Connection connection = getConnection()) {
			return func.apply(connection);
		} catch (final SQLException e) {
			throw ExceptionMapper.create(e)
					.orElse(new DatabaseException("Error getting connection", e));
		}
	}

	public void connected(ConnectedConsumer func) {
		connected(connection -> {
			func.accept(connection);
			return null;
		});
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public HikariConfig getConfig() {
		return config;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	@Nullable
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public String toString() {
		return "DatabaseConnectionManager{" +
				"config=" + config +
				", sqlDialect=" + sqlDialect +
				", dataSource=" + dataSource +
				'}';
	}

	@FunctionalInterface
	public interface ConnectedFunction<R> {
		R apply(Connection connection) throws SQLException;
	}

	@FunctionalInterface
	public interface ConnectedConsumer {
		void accept(Connection connection) throws SQLException;
	}
}
