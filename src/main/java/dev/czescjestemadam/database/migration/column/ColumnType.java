package dev.czescjestemadam.database.migration.column;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;

public enum ColumnType {
	// numeric
	BOOL(boolean.class),

	/// Size: byte
	TINYINT(byte.class),
	/// Size: 2 bytes
	SMALLINT(short.class),
	/// Size: 3 bytes
	MEDIUMINT,
	/// Size: 4 bytes
	INTEGER(int.class),
	/// Size: 8 bytes
	BIGINT(long.class),

	/// Size: 4 bytes
	FLOAT(float.class),
	/// Size: 8 bytes
	DOUBLE(double.class),


	// string
	/// Max size: 255 chars
	CHAR(String.class),
	/// Max size: 255 / 65535 chars
	VARCHAR(String.class),

	/// Max size: 65535 bytes
	TEXT(String.class),
	/// Max size: 16777215 chars
	MEDIUMTEXT(String.class),
	/// Max size: 4294967295 chars
	LONGTEXT(String.class),

	/// Max size: 255 bytes
	TINYBLOB(byte[].class),
	/// Max size: 65535 bytes
	BLOB(byte[].class),
	/// Max size: 4294967295 bytes
	LONGBLOB(byte[].class),


	// date / time
	/// Format: YYYY-MM-DD
	DATE(Date.class),
	//	DATETIME(), // TODO
	TIMESTAMP(Timestamp.class),
	/// Format: hh:mm:ss
	TIME(Time.class),
	YEAR(Year.class),

	;

	private final Class<?> valueClass;

	<T> ColumnType(Class<T> valueClass) {
		this.valueClass = valueClass;
	}

	ColumnType() {
		this(null);
	}

	public Class<?> getValueClass() {
		return valueClass;
	}
}
