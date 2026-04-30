package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.HikariConfigBuilder;
import dev.czescjestemadam.database.exceptions.TransactionException;
import dev.czescjestemadam.database.transaction.TransactionContext;
import dev.czescjestemadam.database.transaction.TransactionExecutor;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionTest {
	private static final Path sqlitePath = Path.of("src/test/resources/test-transaction.db");
	private static final DatabaseConnectionManager MANAGER = new DatabaseConnectionManager(
		new HikariConfigBuilder()
			.sqlite(sqlitePath)
			.build()
	);

	@BeforeAll
	static void setupDatabase() throws Exception {
		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS test_users (" +
			                  "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			                  "name TEXT NOT NULL, " +
			                  "balance INTEGER NOT NULL DEFAULT 0)");
		}
	}

	@AfterAll
	static void cleanupDatabase() throws Exception {
		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			statement.execute("DROP TABLE IF EXISTS test_users");
		}
	}

	@BeforeEach
	void clearTable() throws Exception {
		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			statement.execute("DELETE FROM test_users");
		}
	}

	@Test
	@Order(1)
	void testTransactionCommit() throws Exception {
		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		executor.execute(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Alice', 100)");
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Bob', 200)");
		});

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			assertEquals(2, resultSet.getInt(1));
		}
	}

	@Test
	@Order(2)
	void testTransactionRollback() throws Exception {
		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Charlie', 300)");
		}

		final long initialCount;
		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			initialCount = resultSet.getLong(1);
		}

		assertThrows(
			Exception.class,
			() -> executor.execute((TransactionExecutor.TransactionConsumer)transaction -> {
				final Connection connection = transaction.getConnection();
				final Statement statement = connection.createStatement();
				statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Dave', 400)");
				statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Eve', 500)");

				throw new RuntimeException("Simulated error");
			})
		);

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			assertEquals(initialCount, resultSet.getLong(1));
		}
	}

	@Test
	@Order(3)
	void testNestedOperationsInTransaction() throws Exception {
		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		executor.execute(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement stmt = connection.createStatement();
			stmt.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Frank', 600)");

			stmt.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('Grace', 700)");
		});

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			assertEquals(2, resultSet.getInt(1));
		}
	}

	@Test
	@Order(4)
	void testTransactionContext() {
		assertFalse(TransactionContext.hasActiveTransaction());

		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		executor.execute(transaction -> {
			assertTrue(TransactionContext.hasActiveTransaction());
			assertEquals(transaction, TransactionContext.getCurrentTransaction().orElse(null));
		});

		assertFalse(TransactionContext.hasActiveTransaction());
	}

	@Test
	@Order(5)
	void testMultipleTransactionsSequentially() throws Exception {
		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		executor.execute(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('User1', 100)");
		});

		executor.execute(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('User2', 200)");
		});

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			assertEquals(2, resultSet.getInt(1));
		}
	}

	@Test
	@Order(6)
	void testTransactionWithReturnValue() throws Exception {
		final TransactionExecutor executor = MANAGER.getTransactionExecutor();

		final Long insertedId = executor.execute(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement statement = connection.createStatement();
			statement.executeUpdate(
				"INSERT INTO test_users (name, balance) VALUES ('ReturnValue', 999)",
				Statement.RETURN_GENERATED_KEYS
			);

			final ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			}
			return null;
		});

		assertNotNull(insertedId);

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT * FROM test_users WHERE id = " + insertedId);
			assertTrue(resultSet.next());
			assertEquals("ReturnValue", resultSet.getString("name"));
			assertEquals(999, resultSet.getInt("balance"));
		}
	}

	@Test
	@Order(7)
	void testManagerTransactionMethod() throws Exception {
		MANAGER.transaction(transaction -> {
			final Connection connection = transaction.getConnection();
			final Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO test_users (name, balance) VALUES ('ManagerTest', 123)");
		});

		try (final Connection connection = MANAGER.getConnection()) {
			final Statement statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_users");
			assertTrue(resultSet.next());
			assertEquals(1, resultSet.getInt(1));
		}
	}

	@Test
	@Order(8)
	void testNoActiveTransactionOutsideTransaction() {
		assertFalse(TransactionContext.hasActiveTransaction());
		assertFalse(TransactionContext.getCurrentTransaction().isPresent());
	}

	@Test
	@Order(9)
	void testTransactionContextThrowsWhenNoActiveTransaction() {
		assertThrows(TransactionException.class, TransactionContext::getConnection);
	}
}
