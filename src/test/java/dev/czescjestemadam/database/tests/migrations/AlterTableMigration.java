package dev.czescjestemadam.database.tests.migrations;

import dev.czescjestemadam.database.migration.DatabaseMigration;
import dev.czescjestemadam.database.migration.MigrationBuilder;

public class AlterTableMigration implements DatabaseMigration {
	@Override
	public void up(MigrationBuilder builder) {
		builder.createTable(
			"alter_examples", table -> {
				table.id();
				table.string("name");
			}
		);

		builder.table(
			"alter_examples", table -> {
				table.string("email").nullable();
				table.renameColumn("name", "full_name");
				table.string("temp_col").nullable();
			}
		);

		builder.table("alter_examples", table -> table.dropColumn("temp_col"));
	}

	@Override
	public void down(MigrationBuilder builder) {
		builder.dropTable("alter_examples");
	}
}
