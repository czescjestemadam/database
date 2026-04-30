package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.exceptions.model.ModelNotFoundException;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.ExampleMigration;
import dev.czescjestemadam.database.tests.models.Example;
import dev.czescjestemadam.database.tests.repositories.ExampleRepository;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QueryBuilderFluentTest {
	private static final ExampleMigration MIGRATION = new ExampleMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-query-builder-fluent.db");

	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
		new HikariConfigBuilder()
			.sqlite(sqlitePath)
			.build()
	);

	private static final MigrationManager MIGRATION_MANAGER = new MigrationManager(List.of(), MANAGER);

	private static ExampleRepository repository;

	@BeforeAll
	static void init() {
		MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.UP);
		repository = new ExampleRepository(MANAGER);
	}

	@AfterAll
	static void cleanup() {
		MIGRATION_MANAGER.runMigration(MIGRATION, MigrationAction.DOWN);
	}

	@Test
	@Order(1)
	void testQueryBuilderFirst() {
		final Example model = new Example(
			null,
			"test_user",
			"nullable",
			"dflt",
			"unique_fluent1"
		);
		repository.insert(model);

		final Example result = repository.query()
			.whereEquals("str", "test_user")
			.first();

		assertNotNull(result);
		assertEquals("test_user", result.str);
	}

	@Test
	@Order(2)
	void testQueryBuilderFirstWhenNoMatch() {
		final Example result = repository.query()
			.whereEquals("str", "nonexistent")
			.first();

		assertNull(result);
	}

	@Test
	@Order(3)
	void testQueryBuilderFirstOrFail() {
		final Example model = new Example(
			null,
			"test_user2",
			"nullable",
			"dflt",
			"unique_fluent2"
		);
		repository.insert(model);

		final Example result = repository.query()
			.whereEquals("str", "test_user2")
			.firstOrFail();

		assertNotNull(result);
		assertEquals("test_user2", result.str);
	}

	@Test
	@Order(4)
	void testQueryBuilderFirstOrFailWhenNoMatch() {
		assertThrows(
			ModelNotFoundException.class,
			() -> repository.query()
				.whereEquals("str", "nonexistent")
				.firstOrFail()
		);
	}

	@Test
	@Order(5)
	void testQueryBuilderFirstChaining() {
		final Example model1 = new Example(
			null,
			"chaining_test",
			"nullable1",
			"dflt",
			"unique_chaining1"
		);
		repository.insert(model1);

		final Example model2 = new Example(
			null,
			"other_user",
			"nullable2",
			"dflt",
			"unique_chaining2"
		);
		repository.insert(model2);

		final Example result = repository.query()
			.whereEquals("str", "chaining_test")
			.first();

		assertNotNull(result);
		assertEquals("chaining_test", result.str);
	}

	@Test
	@Order(6)
	void testQueryBuilderFirstStillAllowsLimit() {
		final Example model = new Example(
			null,
			"limit_test",
			"nullable",
			"dflt",
			"unique_limit1"
		);
		repository.insert(model);

		final Example result = repository.query()
			.whereEquals("str", "limit_test")
			.limit(1)
			.first();

		assertNotNull(result);
		assertEquals("limit_test", result.str);
	}

}
