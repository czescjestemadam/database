package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.exceptions.model.ModelNotFoundException;
import dev.czescjestemadam.database.migration.MigrationAction;
import dev.czescjestemadam.database.migration.MigrationManager;
import dev.czescjestemadam.database.tests.migrations.ExampleMigration;
import dev.czescjestemadam.database.tests.models.Example;
import dev.czescjestemadam.database.tests.repositories.ExampleRepository;
import dev.czescjestemadam.database.transaction.TransactionExecutor;
import org.junit.jupiter.api.*;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepositoryTransactionTest {
	private static final ExampleMigration MIGRATION = new ExampleMigration();
	private static final Path sqlitePath = Path.of("src/test/resources/test-repo-transaction.db");

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
	void testRepositoryInTransactionCommit() {
		MANAGER.transaction(transaction -> {
			repository.insert(new Example(
				null,
				"user1",
				"nullable1",
				"dflt1",
				"unique1"
			));

			repository.insert(new Example(
				null,
				"user2",
				"nullable2",
				"dflt2",
				"unique2"
			));
		});

		assertEquals(2, repository.count());
		assertNotNull(repository.find(1));
		assertNotNull(repository.find(2));
	}

	@Test
	@Order(2)
	void testRepositoryInTransactionRollback() {
		final long initialCount = repository.count();

		assertThrows(
			RuntimeException.class,
			() -> MANAGER.transaction((TransactionExecutor.TransactionConsumer)transaction -> {
				final Example model = new Example(
					null,
					"user3",
					"nullable3",
					"dflt3",
					"unique3"
				);
				repository.insert(model);

				throw new RuntimeException("Simulated error");
			})
		);

		assertEquals(initialCount, repository.count());
		assertNull(repository.find(3));
	}

	@Test
	@Order(3)
	void testMixedReadAndWriteInTransaction() {
		final long initialCount = repository.count();

		MANAGER.transaction(transaction -> {
			final long countBefore = repository.count();
			assertEquals(initialCount, countBefore);

			final Example model = new Example(
				null,
				"user4",
				"nullable4",
				"dflt4",
				"unique4"
			);
			repository.insert(model);

			final long countAfter = repository.count();
			assertEquals(countBefore + 1, countAfter);
		});

		assertEquals(initialCount + 1, repository.count());
	}

	@Test
	@Order(4)
	void testUpdateInTransaction() {
		final Example model = new Example(
			null,
			"user5",
			"nullable5",
			"dflt5",
			"unique5"
		);
		repository.insert(model);
		final BigInteger id = model.id;

		MANAGER.transaction(transaction -> {
			final Example fetched = repository.findOrFail(id);
			fetched.str = "updated_str";
			repository.update(fetched);
		});

		final Example updated = repository.findOrFail(id);
		assertEquals("updated_str", updated.str);
	}

	@Test
	@Order(5)
	void testDeleteInTransaction() {
		final Example model1 = new Example(
			null,
			"user6",
			"nullable6",
			"dflt6",
			"unique6"
		);
		final Example model2 = new Example(
			null,
			"user7",
			"nullable7",
			"dflt7",
			"unique7"
		);
		repository.insert(model1);
		repository.insert(model2);

		final long countBefore = repository.count();

		MANAGER.transaction(transaction -> {
			repository.delete(model1.id);
			repository.delete(model2.id);
		});

		assertEquals(countBefore - 2, repository.count());
		assertThrows(ModelNotFoundException.class, () -> repository.findOrFail(model1.id));
		assertThrows(ModelNotFoundException.class, () -> repository.findOrFail(model2.id));
	}

	@Test
	@Order(6)
	void testNonTransactionalStillWorks() {
		final Example model = new Example(
			null,
			"user8",
			"nullable8",
			"dflt8",
			"unique8"
		);
		repository.insert(model);

		assertNotNull(model.id);
		assertNotNull(repository.find(model.id));
	}

	@Test
	@Order(7)
	void testTransactionWithReturnValue() {
		final BigInteger newId = MANAGER.transaction(transaction -> {
			final Example model = new Example(
				null,
				"user9",
				"nullable9",
				"dflt9",
				"unique9"
			);
			repository.insert(model);

			return model.id;
		});

		assertNotNull(newId);
		final Example fetched = repository.findOrFail(newId);
		assertEquals("user9", fetched.str);
	}

	@Test
	@Order(8)
	void testBatchInsertInTransaction() {
		final long initialCount = repository.count();

		MANAGER.transaction(transaction -> {
			final List<Example> models = List.of(
				new Example(
					null,
					"batch1",
					"nullable1",
					"dflt1",
					"unique_batch1"
				),
				new Example(
					null,
					"batch2",
					"nullable2",
					"dflt2",
					"unique_batch2"
				),
				new Example(
					null,
					"batch3",
					"nullable3",
					"dflt3",
					"unique_batch3"
				)
			);

			repository.insert(models);
		});

		assertEquals(initialCount + 3, repository.count());
	}

	@Test
	@Order(9)
	void testInsertValuesInTransaction() {
		final long initialCount = repository.count();

		MANAGER.transaction(transaction -> {
			repository.insertValues(List.of(
				Map.of(
					"str", "val1",
					"str_nullable", "n1",
					"str_dflt", "d1",
					"str_unique", "unique_val1"
				),
				Map.of(
					"str", "val2",
					"str_nullable", "n2",
					"str_dflt", "d2",
					"str_unique", "unique_val2"
				)
			));
		});

		assertEquals(initialCount + 2, repository.count());
	}
}
