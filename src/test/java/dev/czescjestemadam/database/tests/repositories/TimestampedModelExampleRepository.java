package dev.czescjestemadam.database.tests.repositories;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.repository.TimestampedRepository;
import dev.czescjestemadam.database.tests.models.TimestampedModelExample;

public class TimestampedModelExampleRepository extends TimestampedRepository<TimestampedModelExample> {
	public TimestampedModelExampleRepository(DatabaseConnectionManager manager) {
		super(manager, TimestampedModelExample.class, TimestampedModelExample::new);
	}
}