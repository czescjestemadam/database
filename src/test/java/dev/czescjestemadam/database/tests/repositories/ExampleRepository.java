package dev.czescjestemadam.database.tests.repositories;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.repository.AbstractRepository;
import dev.czescjestemadam.database.tests.models.Example;

public class ExampleRepository extends AbstractRepository<Example> {
	public ExampleRepository(DatabaseConnectionManager manager) {
		super(manager, Example.class, Example::new);
	}
}
