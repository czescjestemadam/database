package dev.czescjestemadam.database.exceptions;

public class TransactionException extends DatabaseException {
	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}
}
