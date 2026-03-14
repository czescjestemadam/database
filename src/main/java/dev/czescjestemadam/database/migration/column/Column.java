package dev.czescjestemadam.database.migration.column;

public class Column {
	private final ColumnType type;
	private final int size;
	private final String name;

	private boolean primaryKey;
	private boolean autoIncrement;
	private boolean unique;
	private boolean nullable;
	private boolean unsigned;
	private Object defaultValue;

	public Column(ColumnType type, int size, String name) {
		this.type = type;
		this.size = size;
		this.name = name;
	}

	public ColumnType getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "Column{" +
			"type=" + type +
			", size=" + size +
			", name='" + name + '\'' +
			", primaryKey=" + primaryKey +
			", autoIncrement=" + autoIncrement +
			", unique=" + unique +
			", nullable=" + nullable +
			", unsigned=" + unsigned +
			", defaultValue=" + defaultValue +
			'}';
	}
}
