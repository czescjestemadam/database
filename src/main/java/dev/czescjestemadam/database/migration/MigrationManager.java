package dev.czescjestemadam.database.migration;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.query.Query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {
	private final List<DatabaseMigration> migrations;
	private final DatabaseConnectionManager connectionManager;

	public MigrationManager(List<DatabaseMigration> migrations, DatabaseConnectionManager connectionManager) {
		this.migrations = migrations.stream()
				.sorted(Comparator.comparing(migration -> migration.getClass().getSimpleName()))
				.toList();
		this.connectionManager = connectionManager;
	}

	public void runMigrations() {
		runMigrations(false);
	}

	public void runMigrations(boolean fresh) {
		if (fresh) {
			for (final DatabaseMigration migration : migrations.reversed()) {
				runMigration(migration, MigrationAction.DOWN);
			}
		}

		for (final DatabaseMigration migration : migrations) {
			runMigration(migration, MigrationAction.UP);
		}
	}

	public void runMigration(DatabaseMigration migration, MigrationAction action) {
		final MigrationBuilder builder = new MigrationBuilder(connectionManager.getSqlDialect());
		action.run(migration, builder);

		final Query<?> query = builder.build();

		try (final Connection connection = connectionManager.getConnection()) {
			query.execute(connection);
		} catch (final SQLException e) {
			throw new DatabaseException(
					String.format("Failed to run migration %s (%s)", migration, action),
					e
			);
		}
	}

	@Override
	public String toString() {
		return "MigrationManager{" +
				"migrations=" + migrations +
				", connectionManager=" + connectionManager +
				'}';
	}
}
