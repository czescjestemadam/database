package dev.czescjestemadam.database.migration;

public interface DatabaseMigration {
	void up(MigrationBuilder builder);

	void down(MigrationBuilder builder);
}
