package dev.czescjestemadam.database.exceptions;

public class ModelNotFoundException extends RuntimeException {
	public ModelNotFoundException(String message) {
		super(message);
	}
}
