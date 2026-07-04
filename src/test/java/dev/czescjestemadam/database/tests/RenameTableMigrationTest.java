package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.RenameTableMigration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RenameTableMigrationTest {
	private static final RenameTableMigration MIGRATION = new RenameTableMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-rename.db");

	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
		new HikariConfigBuilder()
			.sqlite(sqlitePath)
			.build()
	);

	private static final MigrationManager MIGRATION_MANAGER = new MigrationManager(List.of(), MANAGER);

	@BeforeAll
	static void init() {
		MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.UP);
	}

	@AfterAll
	static void cleanup() {
		MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN);
	}

	@Test
	void tableWasRenamed() throws SQLException {
		final Set<String> tables = new HashSet<>();

		try (final Connection connection = MANAGER.getConnection()) {
			try (
				final Statement statement = connection.createStatement();
				final ResultSet resultSet = statement.executeQuery(
					"SELECT name FROM sqlite_master WHERE type = 'table'"
				)
			) {

				while (resultSet.next()) {
					tables.add(resultSet.getString("name"));
				}
			}

			try (final Statement statement = connection.createStatement()) {
				statement.execute("SELECT * FROM renamed_examples");
			}
		}

		assertTrue(tables.contains("renamed_examples"), "new table name should exist");
		assertFalse(tables.contains("rename_examples"), "old table name should be gone");
	}

	@Test
	void oldNameIsNoLongerAccessible() {
		final SQLException exception = assertThrows(
			SQLException.class, () -> {
				try (
					final Connection connection = MANAGER.getConnection();
					final Statement statement = connection.createStatement()
				) {
					statement.execute("SELECT * FROM rename_examples");
				}
			}
		);

		assertTrue(exception.getMessage().toLowerCase().contains("no such table"));
	}
}
