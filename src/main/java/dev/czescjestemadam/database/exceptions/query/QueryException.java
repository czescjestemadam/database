package dev.czescjestemadam.database.exceptions.query;

public class QueryException extends RuntimeException {
	public QueryException(String message) {
		super(message);
	}

	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
