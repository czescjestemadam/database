package dev.czescjestemadam.database.transaction;

import dev.czescjestemadam.database.exceptions.TransactionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTransaction implements Transaction {
	private final Connection connection;
	private final boolean autoCommitOriginal;
	private boolean active = true;
	private boolean completed;

	public DatabaseTransaction(DataSource dataSource) throws SQLException {
		connection = dataSource.getConnection();
		autoCommitOriginal = connection.getAutoCommit();

		connection.setAutoCommit(false);
	}

	@Override
	public Connection getConnection() {
		if (!active) {
			throw new TransactionException("Transaction is not active");
		}

		return connection;
	}

	@Override
	public void commit() throws SQLException {
		if (!active || completed) {
			throw new TransactionException("Cannot commit completed or inactive transaction");
		}

		try {
			connection.commit();
			completed = true;
		} catch (final SQLException e) {
			rollback();
			throw e;
		}
	}

	@Override
	public void rollback() throws SQLException {
		if (completed && !active) {
			return;
		}

		try {
			if (!connection.isClosed() && !completed) {
				connection.rollback();
			}
		} finally {
			active = false;
			completed = true;
		}
	}

	@Override
	public void close() throws SQLException {
		try {
			if (!connection.isClosed()) {
				if (!completed && active) {
					rollback();
				}
				connection.setAutoCommit(autoCommitOriginal);
			}
		} finally {
			if (!connection.isClosed()) {
				connection.close();
			}
		}
	}

	@Override
	public boolean isActive() {
		return active && !completed;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}
}
