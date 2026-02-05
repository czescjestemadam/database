package dev.czescjestemadam.database.exceptions.constraint;

public class UniqueConstraintException extends ConstraintException {
	public UniqueConstraintException(String message) {
		super(message);
	}

	public UniqueConstraintException(String message, Throwable cause) {
		super(message, cause);
	}
}
