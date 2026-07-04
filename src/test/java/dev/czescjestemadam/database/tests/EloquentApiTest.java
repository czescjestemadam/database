package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.ExampleMigration;
import dev.czescjestemadam.database.tests.models.Example;
import dev.czescjestemadam.database.tests.repositories.ExampleRepository;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EloquentApiTest {
	private static final ExampleMigration MIGRATION = new ExampleMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-eloquent.db");

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
	void saveInsertsWhenNoId() {
		final Example model = new Example(null, "save_insert", null, "dflt", "save_insert_unique");

		final Example saved = repository.save(model);

		assertNotNull(saved.getId());
		assertEquals(saved, repository.findOrFail(saved.getId()));
	}

	@Test
	@Order(2)
	void saveUpdatesWhenIdPresent() {
		final Example model = new Example(null, "save_update", null, "dflt", "save_update_unique");
		repository.save(model);

		model.str = "save_updated";
		repository.save(model);

		assertEquals("save_updated", repository.findOrFail(model.getId()).str);
	}

	@Test
	@Order(3)
	void allReturnsEverything() {
		final List<Example> all = repository.all();

		assertEquals(repository.count(), all.size());
		assertTrue(all.stream().anyMatch(example -> "save_insert".equals(example.str)));
	}

	@Test
	@Order(4)
	void countWithConditions() {
		assertEquals(1, repository.count(repository.query().whereEquals("str", "save_insert")));
		assertEquals(0, repository.count(repository.query().whereEquals("str", "does_not_exist")));
	}

	@Test
	@Order(5)
	void existsWithConditions() {
		assertTrue(repository.exists(repository.query().whereEquals("str", "save_insert")));
		assertFalse(repository.exists(repository.query().whereEquals("str", "does_not_exist")));
	}

	@Test
	@Order(6)
	void createFromValuesMap() {
		final Example created = repository.create(Map.of(
			"str", "create_str",
			"str_dflt", "dflt",
			"str_unique", "create_unique"
		));

		assertNotNull(created.getId());
		assertEquals("create_str", repository.findOrFail(created.getId()).str);
	}

	@Test
	@Order(7)
	void firstOrCreateCreatesWhenMissing() {
		final Example created = repository.firstOrCreate(
			Map.of("str_unique", "foc_unique"),
			Map.of("str", "foc_str", "str_dflt", "dflt")
		);

		assertNotNull(created.getId());

		final Example again = repository.firstOrCreate(
			Map.of("str_unique", "foc_unique"),
			Map.of("str", "foc_str", "str_dflt", "dflt")
		);

		assertEquals(created.getId(), again.getId());
	}

	@Test
	@Order(8)
	void updateOrCreateCreatesThenUpdates() {
		final Example created = repository.updateOrCreate(
			Map.of("str_unique", "uoc_unique"),
			Map.of("str", "uoc_str", "str_dflt", "dflt")
		);

		final Example updated = repository.updateOrCreate(
			Map.of("str_unique", "uoc_unique"),
			Map.of("str", "uoc_updated_str", "str_dflt", "dflt")
		);

		assertEquals(created.getId(), updated.getId());
		assertEquals("uoc_updated_str", repository.findOrFail(created.getId()).str);
	}

	@Test
	@Order(9)
	void deleteByQueryIsBatched() {
		for (int i = 0; i < 3; i++) {
			repository.create(Map.of(
				"str", "batch_del",
				"str_dflt", "dflt",
				"str_unique", "batch_del_" + i
			));
		}

		final int affected = repository.delete(repository.query().whereEquals("str", "batch_del"));

		assertEquals(3, affected);
		assertEquals(0, repository.count(repository.query().whereEquals("str", "batch_del")));
	}

	@Test
	@Order(10)
	void fillSetsValues() {
		final Example model = new Example();

		model.fill(Map.of(
			"str", "filled",
			"str_dflt", "dflt",
			"str_unique", "filled_unique"
		));

		assertEquals("filled", model.str);
		assertEquals("filled_unique", model.strUnique);
	}
}
