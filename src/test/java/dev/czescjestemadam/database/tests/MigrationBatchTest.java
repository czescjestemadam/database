package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.migration.batches.MigrationRepository;
import dev.czescjestemadam.database.tests.migrations.ExampleMigration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MigrationBatchTest {
	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
		new HikariConfigBuilder()
			.sqlite(Path.of("src/test/resources/test-migrations.db"))
			.build()
	);

	private static final ExampleMigration MIGRATION = new ExampleMigration();

	private static final MigrationManager MIGRATION_MANAGER = new MigrationManager(
		List.of(MIGRATION),
		MANAGER
	);
	private static final MigrationRepository MIGRATION_REPOSITORY = new MigrationRepository(MANAGER);

	@Test
	@Order(100)
	void run() {
		MIGRATION_MANAGER.runMigrations();

		assertNotNull(MIGRATION_REPOSITORY.findMigration(MIGRATION));
	}

	@Test
	@Order(200)
	void rollback() {
		MIGRATION_MANAGER.rollbackMigrations();
		assertNull(MIGRATION_REPOSITORY.findMigration(MIGRATION));
	}
}
