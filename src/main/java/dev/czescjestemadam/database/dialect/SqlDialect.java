package dev.czescjestemadam.database.dialect;

import dev.czescjestemadam.database.migration.column.ColumnType;

public enum SqlDialect {
	SQLITE("AUTOINCREMENT", ColumnType.INTEGER),
	MYSQL("AUTO_INCREMENT", ColumnType.BIGINT),

	;

	private final String autoIncrement;
	private final ColumnType idColumnType;

	SqlDialect(String autoIncrement, ColumnType idColumnType) {
		this.autoIncrement = autoIncrement;
		this.idColumnType = idColumnType;
	}

	public String getAutoIncrement() {
		return autoIncrement;
	}

	public ColumnType getIdColumnType() {
		return idColumnType;
	}

	public static SqlDialect detect(String jdbcUrl) {
		for (final SqlDialect dialect : values()) {
			if (jdbcUrl.startsWith("jdbc:" + dialect.name().toLowerCase())) {
				return dialect;
			}
		}

		throw new IllegalArgumentException("Unexpected sql dialect");
	}
}
