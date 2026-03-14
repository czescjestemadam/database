package dev.czescjestemadam.database.tests;

import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.model.annotations.Table;
import dev.czescjestemadam.database.tests.models.Example;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
	@Test
	void tableName() {
		assertEquals("examples", Model.getTableName(Example.class));
		assertEquals("table_name_tests", Model.getTableName(TableNameTest.class));
		assertEquals("other_table_name_tests", Model.getTableName(OtherTableNameTests.class));
		assertEquals("AnnotatedModel", Model.getTableName(AnnotatedModel.class));
	}

	@Test
	void copy() {
		final Example example = new Example(BigInteger.ONE, "str", null, "not_dflt", "unique");
		final Example copy = example.copy();
		assertNotSame(example, copy);
		assertEquals(example, copy);

		copy.id = BigInteger.ZERO;
		assertNotEquals(example, copy);
	}

	@Test
	void original() {
		final Example example = new Example(BigInteger.ONE, "str", null, "not_dflt", "unique");
		assertNull(example.getOriginal());

		example.copyToOriginal();
		assertFalse(example.isDirty());

		example.id = BigInteger.ZERO;
		assertTrue(example.isDirty());
	}

	@Test
	void getValues() {
		final Example example = new Example(BigInteger.ONE, "str", null, "not_dflt", "unique");

		final Map<String, Object> values = new HashMap<>();
		values.put("id", BigInteger.ONE);
		values.put("str", "str");
		values.put("str_nullable", null);
		values.put("str_dflt", "not_dflt");
		values.put("str_unique", "unique");

		assertEquals(values, example.getValues());
	}

	@Test
	void setValues() {
		final Example example = new Example(BigInteger.ONE, "str", null, "not_dflt", "unique");

		final Example example2 = new Example();

		final Map<String, Object> values = new HashMap<>();
		values.put("id", BigInteger.ONE);
		values.put("str", "str");
		values.put("str_nullable", null);
		values.put("str_dflt", "not_dflt");
		values.put("str_unique", "unique");

		example2.setValues(values);

		assertEquals(example, example2);
	}


	private static class TableNameTest extends Model<TableNameTest> {
		@Override
		public TableNameTest copy() {
			return null;
		}

		@Override
		public BigInteger getId() {
			return BigInteger.ZERO;
		}
	}

	private static class OtherTableNameTests extends Model<OtherTableNameTests> {
		@Override
		public OtherTableNameTests copy() {
			return null;
		}

		@Override
		public BigInteger getId() {
			return BigInteger.ZERO;
		}
	}

	@Table("AnnotatedModel")
	private static class AnnotatedModel extends Model<AnnotatedModel> {
		@Override
		public AnnotatedModel copy() {
			return null;
		}

		@Override
		public BigInteger getId() {
			return BigInteger.ZERO;
		}
	}
}
