package dev.czescjestemadam.database.dialect;

public enum SqlDialect {
	SQLITE("AUTOINCREMENT"),
	MYSQL("AUTO_INCREMENT"),

	;

	private final String autoIncrement;

	SqlDialect(String autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getAutoIncrement() {
		return autoIncrement;
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
