package dev.czescjestemadam.database.exceptions.model;

public class ModelNotFoundException extends RuntimeException {
	public ModelNotFoundException(String message) {
		super(message);
	}
}
