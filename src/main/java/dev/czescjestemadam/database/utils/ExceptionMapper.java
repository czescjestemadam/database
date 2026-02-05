package dev.czescjestemadam.database.utils;

import dev.czescjestemadam.database.exceptions.constraint.UniqueConstraintException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.Optional;

public abstract class ExceptionMapper {
	public static Optional<RuntimeException> create(SQLException exception) {
		if (exception instanceof SQLiteException sqLiteException) {
			return Optional.ofNullable(createFromSqlite(sqLiteException));
		}

		return Optional.empty();
	}

	private static RuntimeException createFromSqlite(SQLiteException exception) {
		if (exception.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
			return new UniqueConstraintException(exception.getMessage(), exception);
		}

		return null;
	}
}
