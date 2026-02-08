package dev.czescjestemadam.database.migration;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.migration.batches.MigrationModel;
import dev.czescjestemadam.database.migration.batches.MigrationRepository;
import dev.czescjestemadam.database.query.OrderType;
import dev.czescjestemadam.database.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MigrationManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationManager.class);

	private final List<DatabaseMigration> migrations;
	private final DatabaseConnectionManager connectionManager;
	private final MigrationRepository repository;

	public MigrationManager(List<DatabaseMigration> migrations, DatabaseConnectionManager connectionManager) {
		this.migrations = migrations.stream()
				.sorted(Comparator.comparing(DatabaseMigration::getName))
				.toList();
		this.connectionManager = connectionManager;
		this.repository = new MigrationRepository(connectionManager);
	}

	public void runMigrations() {
		runMigrations(false);
	}

	public void runMigrations(boolean fresh) {
		if (fresh) {
			for (final DatabaseMigration migration : migrations.reversed()) {

				final MigrationModel migrationModel = repository.findMigration(migration);
				if (migrationModel != null) {
					runMigration(migration, MigrationAction.DOWN);
					repository.delete(migrationModel);
				} else {
					LOGGER.warn("No table found for {}", migration.getName());
				}
			}
		}

		final long batchId = repository.count();
		final List<String> migrationNames = new ArrayList<>();

		for (final DatabaseMigration migration : migrations) {
			final MigrationModel migrationModel = repository.findMigration(migration);

			if (migrationModel == null) {
				runMigration(migration, MigrationAction.UP);
				migrationNames.add(migration.getName());
			}
		}

		if (!migrationNames.isEmpty()) {
			repository.insert(
					migrationNames.stream()
							.map(name -> new MigrationModel((int)batchId, name))
							.toList()
			);
		}
	}

	public void rollbackMigrations() {
		final MigrationModel latestMigration = repository.first(
				repository.query()
						.orderBy("batch_id", OrderType.DESC)
		);

		if (latestMigration == null) {
			return;
		}

		final List<MigrationModel> batchMigrationModels = repository.select(
				repository.query()
						.whereEquals("batch_id", latestMigration.batchId)
						.select()
		);

		final List<String> batchMigrationNames = batchMigrationModels.stream()
				.map(migration -> migration.name)
				.toList();

		LOGGER.info("Rolling back migration batch {} from {}", latestMigration.batchId, latestMigration.createdAt);
		for (final DatabaseMigration batchMigration : migrations.reversed()) {
			if (batchMigrationNames.contains(batchMigration.getName())) {
				runMigration(batchMigration, MigrationAction.DOWN);
			}
		}

		// TODO: repo batch delete method
		for (final MigrationModel batchMigrationModel : batchMigrationModels) {
			repository.delete(batchMigrationModel);
		}
	}

	public void runMigration(DatabaseMigration migration, MigrationAction action) {
		final MigrationBuilder builder = new MigrationBuilder(connectionManager.getSqlDialect());
		action.run(migration, builder);

		final Query<?> query = builder.build();

		try (final Connection connection = connectionManager.getConnection()) {
			query.execute(connection);
		} catch (final SQLException e) {
			throw new DatabaseException(
					String.format("Failed to run migration %s (%s)", migration, action),
					e
			);
		}

		LOGGER.info("{} {}", migration.getName(), action.name());
	}

	@Override
	public String toString() {
		return "MigrationManager{" +
				"migrations=" + migrations +
				", connectionManager=" + connectionManager +
				", repository=" + repository +
				'}';
	}
}
