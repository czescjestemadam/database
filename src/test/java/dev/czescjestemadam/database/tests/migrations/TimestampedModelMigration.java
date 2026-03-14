package dev.czescjestemadam.database.tests.migrations;

import dev.czescjestemadam.database.migration.DatabaseMigration;
import dev.czescjestemadam.database.migration.MigrationBuilder;
import dev.czescjestemadam.database.tests.models.TimestampedModelExample;

public class TimestampedModelMigration implements DatabaseMigration {
	@Override
	public void up(MigrationBuilder builder) {
		builder.createTable(
			TimestampedModelExample.class,
			table -> {
				table.id();
				table.string("name");
				table.timestamps();
			}
		);
	}

	@Override
	public void down(MigrationBuilder builder) {
		builder.dropTable(TimestampedModelExample.class);
	}
}