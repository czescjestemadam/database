package dev.czescjestemadam.database.migration.batches;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.migration.DatabaseMigration;
import dev.czescjestemadam.database.migration.MigrationBuilder;
import dev.czescjestemadam.database.query.Query;
import dev.czescjestemadam.database.repository.AbstractRepository;

public class MigrationRepository extends AbstractRepository<MigrationModel> {
	public MigrationRepository(DatabaseConnectionManager manager) {
		super(manager, MigrationModel.class, MigrationModel::new);
		createMigrationsTable();
	}

	public MigrationModel findMigration(DatabaseMigration migration) {
		return first(query().whereEquals("name", migration.getName()));
	}

	private void createMigrationsTable() {
		manager.connected(connection -> {
			final MigrationBuilder migrationBuilder = new MigrationBuilder(manager.getSqlDialect());

			migrationBuilder.createTableIfNotExists(
				modelClass,
				tableBuilder -> {
					tableBuilder.id();
					tableBuilder.integer("batch_id");
					tableBuilder.string("name")
						.unique();
					tableBuilder.timestamp("created_at");
				}
			);

			for (final Query<?> query : migrationBuilder.build()) {
				query.execute(connection);
			}
		});
	}
}
