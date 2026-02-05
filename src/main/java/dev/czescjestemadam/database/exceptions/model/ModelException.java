package dev.czescjestemadam.database.exceptions.model;

import dev.czescjestemadam.database.exceptions.DatabaseException;

public class ModelException extends DatabaseException {
	public ModelException(String message) {
		super(message);
	}

	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}
}
