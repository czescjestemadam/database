package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.dialect.SqlDialect;
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
}
