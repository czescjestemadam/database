package dev.czescjestemadam.database.tests.migrations;

import dev.czescjestemadam.database.migration.DatabaseMigration;
import dev.czescjestemadam.database.migration.MigrationBuilder;

public class RenameTableMigration implements DatabaseMigration {
	@Override
	public void up(MigrationBuilder builder) {
		builder.createTable(
			"rename_examples", table -> {
				table.id();
				table.string("name");
			}
		);

		builder.table("rename_examples", table -> table.rename("renamed_examples"));
	}

	@Override
	public void down(MigrationBuilder builder) {
		builder.dropTable("renamed_examples");
	}
}
