package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.AlterTableMigration;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlterTableMigrationTest {
	private static final AlterTableMigration MIGRATION = new AlterTableMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-alter.db");

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
	@Order(1)
	void addRenameAndDropColumns() throws SQLException {
		final Set<String> columns = new HashSet<>();

		try (final Connection connection = MANAGER.getConnection()) {
			try (
				final Statement statement = connection.createStatement();
				final ResultSet resultSet = statement.executeQuery("PRAGMA table_info(alter_examples)")
			) {

				while (resultSet.next()) {
					columns.add(resultSet.getString("name"));
				}
			}
		}

		assertTrue(columns.contains("id"));
		assertTrue(columns.contains("full_name"), "renamed column should exist");
		assertTrue(columns.contains("email"), "added column should exist");
		assertFalse(columns.contains("name"), "old column name should be gone");
		assertFalse(columns.contains("temp_col"), "dropped column should be gone");
	}
}
