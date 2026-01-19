package dev.czescjestemadam.database.migration.column;

public class ColumnBuilder {
	private final Column column;

	public ColumnBuilder(ColumnType type, int size, String name) {
		column = new Column(type, size, name);
	}

	public ColumnBuilder primaryKey() {
		return primaryKey(true);
	}

	public ColumnBuilder primaryKey(boolean primaryKey) {
		column.setPrimaryKey(primaryKey);
		return this;
	}

	public ColumnBuilder autoIncrement() {
		return autoIncrement(true);
	}

	public ColumnBuilder autoIncrement(boolean autoIncrement) {
		column.setAutoIncrement(autoIncrement);
		return this;
	}

	public ColumnBuilder unique() {
		return unique(true);
	}

	public ColumnBuilder unique(boolean unique) {
		column.setUnique(unique);
		return this;
	}

	public ColumnBuilder nullable() {
		return nullable(true);
	}

	public ColumnBuilder nullable(boolean nullable) {
		column.setNullable(nullable);
		return this;
	}

	public ColumnBuilder withDefault(Object defaultValue) {
		column.setDefaultValue(defaultValue);
		return this;
	}


	public Column build() {
		return column;
	}
}
