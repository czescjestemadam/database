package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.query.builder.InsertQueryBuilder;
import dev.czescjestemadam.database.query.builder.QueryBuilder;
import dev.czescjestemadam.database.query.impl.SelectQuery;
import dev.czescjestemadam.database.query.impl.UpdateQuery;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void limitBuilder() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.limit(10)
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE str = ? LIMIT 10;",
			query.getSql()
		);
	}

	@Test
	void limitInvalid() {
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> new QueryBuilder("examples", Set.of()).limit(0)
		);
	}

	@Test
	void offsetBuilder() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.limit(10)
			.offset(20)
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE str = ? LIMIT 10 OFFSET 20;",
			query.getSql()
		);
	}

	@Test
	void offsetOnly() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.offset(100)
			.select();

		assertEquals(
			"SELECT * FROM examples;",
			query.getSql()
		);
	}

	@Test
	void offsetInvalid() {
		assertThrowsExactly(
			IllegalArgumentException.class,
			() -> new QueryBuilder("examples", Set.of()).offset(-1)
		);
	}

	@Test
	void pagination() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.orderBy("created_at", dev.czescjestemadam.database.query.OrderType.DESC)
			.limit(25)
			.offset(50)
			.select();

		assertEquals(
			"SELECT * FROM examples ORDER BY created_at DESC LIMIT 25 OFFSET 50;",
			query.getSql()
		);
	}

	@Test
	void offsetWithUpdate() {
		final UpdateQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "asd")
			.limit(1)
			.offset(5)
			.update("str", "updated");

		assertEquals(
			"UPDATE examples SET str = ? WHERE str = ? LIMIT 1 OFFSET 5",
			query.getSql()
		);
	}

	@Test
	void offsetWithDelete() {
		final UpdateQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("str", "old")
			.limit(100)
			.offset(500)
			.delete();

		assertEquals(
			"DELETE FROM examples WHERE str = ? LIMIT 100 OFFSET 500",
			query.getSql()
		);
	}

	@Test
	void whereIn() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereIn("status", "active", "pending", "archived")
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE status IN (?, ?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "active", "pending", "archived" },
			query.getParameters().toArray()
		);
	}

	@Test
	void whereInList() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereIn("id", List.of(1, 2, 3, 4, 5))
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE id IN (?, ?, ?, ?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ 1, 2, 3, 4, 5 },
			query.getParameters().toArray()
		);
	}

	@Test
	void whereNotIn() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereNotIn("status", "deleted", "banned")
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE status NOT IN (?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "deleted", "banned" },
			query.getParameters().toArray()
		);
	}

	@Test
	void orWhereIn() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("status", "active")
			.orWhereIn("id", 1, 2, 3)
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE status = ? OR id IN (?, ?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "active", 1, 2, 3 },
			query.getParameters().toArray()
		);
	}

	@Test
	void orWhereNotIn() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereEquals("type", "user")
			.orWhereNotIn("status", "deleted", "banned")
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE type = ? OR status NOT IN (?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "user", "deleted", "banned" },
			query.getParameters().toArray()
		);
	}

	@Test
	void inWithLimit() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereIn("category", List.of("news", "sports"))
			.limit(10)
			.offset(5)
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE category IN (?, ?) LIMIT 10 OFFSET 5;",
			query.getSql()
		);
	}

	@Test
	void inWithDelete() {
		final UpdateQuery query = new QueryBuilder("examples", Set.of())
			.whereNotIn("id", 1, 2, 3)
			.delete();

		assertEquals(
			"DELETE FROM examples WHERE id NOT IN (?, ?, ?)",
			query.getSql()
		);
	}

	@Test
	void emptyIn() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereIn("status", List.of())
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE status IN ();",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{},
			query.getParameters().toArray()
		);
	}

	@Test
	void multipleInConditions() {
		final SelectQuery query = new QueryBuilder("examples", Set.of())
			.whereIn("status", "active", "pending")
			.whereNotIn("type", "system", "temp")
			.select();

		assertEquals(
			"SELECT * FROM examples WHERE status IN (?, ?) AND type NOT IN (?, ?);",
			query.getSql()
		);

		assertArrayEquals(
			new Object[]{ "active", "pending", "system", "temp" },
			query.getParameters().toArray()
		);
	}
}
