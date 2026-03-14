package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.query.builder.InsertQueryBuilder;
import dev.czescjestemadam.database.query.builder.QueryBuilder;
import dev.czescjestemadam.database.query.impl.SelectQuery;
import dev.czescjestemadam.database.query.impl.UpdateQuery;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryBuilderTest {
	@Test
	void selectBuilder() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.whereNotNull("str_nullable")
			.orWhereEquals("str_dflt", "not_dflt")
			.orderByDesc("id")
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE str = ? AND str_nullable IS NOT NULL OR str_dflt = ? ORDER BY id DESC;",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "asd", "not_dflt" },
			query.getParameters().toArray()
		);
	}

	@Test
	void updateBuilder() {
		final UpdateQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.whereNull("str_nullable")
			.update(
				"str", "str",
				"str_dflt", "dflt"
			);

		assertEquals(
			"UPDATE examples SET str = ?, str_dflt = ? WHERE str = ? AND str_nullable IS NULL",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "str", "dflt", "asd" },
			query.getParameters().toArray()
		);
	}

	@Test
	void deleteBuilder() {
		final UpdateQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.whereEquals("str_unique", "unique")
			.delete();

		assertEquals(
			"DELETE FROM examples WHERE str = ? AND str_unique = ?",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "asd", "unique" },
			query.getParameters().toArray()
		);
	}

	@Test
	void insertBuilder() {
		final UpdateQuery query = new InsertQueryBuilder("examples")
			.columns("str", "str_nullable", "str_dflt", "str_unique")
			.values("a", "b", "c", "d")
			.values(1, 2, 3, 4)
			.build();

		assertEquals(
			"INSERT INTO examples (str, str_nullable, str_dflt, str_unique) VALUES (?, ?, ?, ?), (?, ?, ?, ?)",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "a", "b", "c", "d", 1, 2, 3, 4 },
			query.getParameters().toArray()
		);
	}
}
