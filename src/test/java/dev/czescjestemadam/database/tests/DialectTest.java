package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.migration.MigrationBuilder;
import dev.czescjestemadam.database.query.AbstractPreparedQuery;
import dev.czescjestemadam.database.query.Query;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class DialectTest {
	@Test
	void sqlite() {
		assertEquals(SqlDialect.SQLITE, SqlDialect.detect("jdbc:sqlite:test.db"));
	}

	@Test
	void mysql() {
		assertEquals(SqlDialect.MYSQL, SqlDialect.detect("jdbc:mysql:localhost:3306/db"));
	}

	@Test
	void other() {
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> SqlDialect.detect("")
		);
	}

	@Test
	void unsignedMysql() {
		final MigrationBuilder builder = new MigrationBuilder(SqlDialect.MYSQL);
		builder.createTableIfNotExists(
			"test", tableBuilder -> {
				tableBuilder.integer("age").unsigned();
				tableBuilder.bigInt("user_id").unsigned();
				tableBuilder.tinyInt("status");
			}
		);

		final Query<?> query = builder.build().getFirst();
		final String sql = ((AbstractPreparedQuery<?>)query).getSql();

		assertEquals(
			"CREATE TABLE IF NOT EXISTS test (age INTEGER UNSIGNED NOT NULL, " +
			"user_id BIGINT UNSIGNED NOT NULL, " +
			"status TINYINT NOT NULL);",
			sql
		);
	}

	@Test
	void unsignedSqlite() {
		final MigrationBuilder builder = new MigrationBuilder(SqlDialect.SQLITE);
		builder.createTableIfNotExists(
			"test", tableBuilder -> {
				tableBuilder.integer("age").unsigned();
				tableBuilder.bigInt("user_id").unsigned();
				tableBuilder.tinyInt("status");
			}
		);

		final Query<?> query = builder.build().getFirst();
		final String sql = ((AbstractPreparedQuery<?>)query).getSql();

		assertEquals(
			"CREATE TABLE IF NOT EXISTS test (age INTEGER NOT NULL, " +
			"user_id BIGINT NOT NULL, " +
			"status TINYINT NOT NULL);",
			sql
		);
	}
}
