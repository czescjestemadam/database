package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.TimestampedModelMigration;
import dev.czescjestemadam.database.tests.models.TimestampedModelExample;
import dev.czescjestemadam.database.tests.repositories.TimestampedModelExampleRepository;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimestampedRepositoryTest {
	private static final TimestampedModelMigration MIGRATION = new TimestampedModelMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-timestamped.db");

	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
		new HikariConfigBuilder()
			.sqlite(sqlitePath)
			.build()
	);

	private static final MigrationManager MIGRATION_MANAGER = new MigrationManager(List.of(), MANAGER);

	private static TimestampedModelExampleRepository repository;

	@BeforeAll
	static void init() {
		assertThrowsExactly(
			DatabaseException.class,
			() -> MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN)
		);

		repository = new TimestampedModelExampleRepository(MANAGER);
	}

	@Test
	@Order(100)
	void migrationUp() {
		assertDoesNotThrow(() -> MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.UP));
	}

	@Test
	@Order(110)
	void insertSetsCreatedAt() {
		final TimestampedModelExample model = new TimestampedModelExample(
			null,
			"test",
			null,
			null
		);
		final long beforeInsert = System.currentTimeMillis();

		repository.insert(model);

		assertNotNull(model.createdAt, "createdAt should be set after insert");
		assertNotNull(model.updatedAt, "updatedAt should be set after insert");
		assertNotNull(model.id, "id should be set after insert");
		assertEquals(model.createdAt, model.updatedAt, "createdAt and updatedAt should be equal on insert");
		assertTrue(
			model.createdAt.getTime() >= beforeInsert &&
				model.createdAt.getTime() <= System.currentTimeMillis() + 1000,
			"createdAt should be close to current time"
		);
	}

	@Test
	@Order(120)
	void updateSetsUpdatedAt() {
		final TimestampedModelExample model = new TimestampedModelExample(
			null,
			"test",
			null,
			null
		);
		repository.insert(model);

		final Timestamp originalCreatedAt = model.createdAt;
		final Timestamp originalUpdatedAt = model.updatedAt;

		Thread.yield(); // Allow time to pass
		model.name = "updated";
		repository.update(model);

		assertNotNull(model.updatedAt, "updatedAt should be set after update");
		assertEquals(originalCreatedAt, model.createdAt, "createdAt should not change on update");
		assertNotEquals(originalUpdatedAt, model.updatedAt, "updatedAt should change on update");
		assertTrue(
			model.updatedAt.after(originalUpdatedAt),
			"updatedAt should be after previous updatedAt"
		);
	}

	@Test
	@Order(130)
	void insertRespectsExistingCreatedAt() {
		final Timestamp customCreatedAt = Timestamp.from(Instant.now().minus(Duration.ofDays(1)));
		final TimestampedModelExample model = new TimestampedModelExample(
			null,
			"test",
			customCreatedAt,
			null
		);

		repository.insert(model);

		assertEquals(
			customCreatedAt.getTime(),
			model.createdAt.getTime(),
			"Existing createdAt should be preserved (in milliseconds)"
		);
	}

	@Test
	@Order(140)
	void secondUpdateUpdatesUpdatedAt() {
		final TimestampedModelExample model = new TimestampedModelExample(null, "test", null, null);
		repository.insert(model);
		repository.update(model);
		final Timestamp firstUpdatedAt = model.updatedAt;

		Thread.yield();
		model.name = "updated again";
		repository.update(model);

		assertNotEquals(firstUpdatedAt, model.updatedAt, "updatedAt should change on each update");
		assertTrue(
			model.updatedAt.after(firstUpdatedAt),
			"Second updatedAt should be after first updatedAt"
		);
	}

	@Test
	@Order(200)
	void migrationDown() {
		assertDoesNotThrow(() -> MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN));
	}
}
