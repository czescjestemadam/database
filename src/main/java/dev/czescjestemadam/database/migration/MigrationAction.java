package dev.czescjestemadam.database.migration;

import java.util.function.BiConsumer;

public enum MigrationAction {
	UP(DatabaseMigration::up),
	DOWN(DatabaseMigration::down),

	;

	private final BiConsumer<DatabaseMigration, MigrationBuilder> function;

	MigrationAction(BiConsumer<DatabaseMigration, MigrationBuilder> function) {
		this.function = function;
	}

	public void run(DatabaseMigration migration, MigrationBuilder builder) {
		function.accept(migration, builder);
	}
}
