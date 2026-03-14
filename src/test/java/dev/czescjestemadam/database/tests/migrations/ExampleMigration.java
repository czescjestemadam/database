package dev.czescjestemadam.database.tests.migrations;

import dev.czescjestemadam.database.migration.DatabaseMigration;
import dev.czescjestemadam.database.migration.MigrationBuilder;
import dev.czescjestemadam.database.tests.models.Example;

public class ExampleMigration implements DatabaseMigration {
	@Override
	public void up(MigrationBuilder builder) {
		builder.createTable(
			Example.class,
			table -> {
				table.id();

				table.string("str");

				table.string("str_nullable")
					.nullable();

				table.string("str_dflt")
					.withDefault("dflt_str");

				table.string("str_unique")
					.unique();
			}
		);
	}

	@Override
	public void down(MigrationBuilder builder) {
		builder.dropTable(Example.class);
	}
}
