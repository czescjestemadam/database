package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.exceptions.ModelNotFoundException;
import dev.czescjestemadam.database.exceptions.constraint.UniqueConstraintException;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.repository.Repository;
import dev.czescjestemadam.database.tests.migrations.ExampleMigration;
import dev.czescjestemadam.database.tests.models.Example;
import dev.czescjestemadam.database.tests.repositories.ExampleRepository;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseTest {
	private static final ExampleMigration MIGRATION = new ExampleMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test.db");

	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
			new HikariConfigBuilder()
					.sqlite(sqlitePath)
					.build()
	);

	private static final MigrationManager MIGRATION_MANAGER = new  MigrationManager(List.of(), MANAGER);

	private static Repository<Example> repository;

	@BeforeAll
	static void init() throws Exception {
		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT 'test';");

			if (resultSet.next()) {
				assertEquals("test", resultSet.getString(1));
			} else {
				fail();
			}
		}

		assertThrowsExactly(
				DatabaseException.class,
				() -> MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN)
		);

		repository = new ExampleRepository(MANAGER);
	}

	@Test
	@Order(100)
	void migrationUp() {
		assertDoesNotThrow(() -> {
			MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.UP);
		});
	}

	@Test
	@Order(110)
	void defaultRepoInitState() {
		assertEquals(0, repository.count());
		assertNull(repository.find(0));

		assertThrowsExactly(
				ModelNotFoundException.class,
				() -> repository.findOrFail(0)
		);
	}

	@Test
	@Order(120)
	void defaultRepoInsertFindUpdate() {
		final Example inserted = new Example(
				null,
				"default_repo_str",
				"default_repo_nullable",
				"default_repo_dflt",
				"default_repo_unique"
		);

		repository.insert(inserted);

		assertEquals(1, repository.count());
		assertNotNull(inserted.id);

		assertEquals(inserted, repository.findOrFail(inserted.id));

		final Example updated = inserted.copy();
		updated.strNullable = null;
		repository.update(updated);

		assertNull(repository.findOrFail(updated.id).strNullable);
	}

	@Test
	@Order(125)
	void defaultRepoDoubleInsert() {
		final Example inserted = new Example(
				null,
				"default_repo_str",
				"default_repo_nullable",
				"default_repo_dflt",
				"default_repo_unique"
		);

		assertThrowsExactly(
				UniqueConstraintException.class,
				() -> repository.insert(inserted)
		);
	}

	@Test
	@Order(130)
	void defaultRepoDelete() {
		assertTrue(repository.delete(repository.findOrFail(1)));
		assertEquals(0, repository.count());
	}

	@Test
	@Order(200)
	void migrationDown() {
		assertDoesNotThrow(() -> {
			MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN);
		});
	}
}
