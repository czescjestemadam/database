package dev.czescjestemadam.database.exceptions.constraint;

import dev.czescjestemadam.database.exceptions.DatabaseException;

public class ConstraintException extends DatabaseException {
	public ConstraintException(String message) {
		super(message);
	}

	public ConstraintException(String message, Throwable cause) {
		super(message, cause);
	}
}
