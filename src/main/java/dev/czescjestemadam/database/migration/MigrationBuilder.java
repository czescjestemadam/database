package dev.czescjestemadam.database.migration;

import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.migration.table.AlterTableBuilder;
import dev.czescjestemadam.database.migration.table.Table;
import dev.czescjestemadam.database.migration.table.TableBuilder;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.query.Query;
import dev.czescjestemadam.database.query.TableQueryAction;
import dev.czescjestemadam.database.query.impl.SimpleQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MigrationBuilder {
	private final List<TableBuilder> tableBuilders = new ArrayList<>();
	private final List<AlterTableBuilder> alterTables = new ArrayList<>();
	private final Set<String> tablesToDrop = new HashSet<>();
	private final SqlDialect sqlDialect;

	public MigrationBuilder(SqlDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	public void createTable(Class<? extends Model<?>> modelClass, Consumer<TableBuilder> builder) {
		table(Model.getTableName(modelClass), TableQueryAction.CREATE, builder, false);
	}

	public void createTable(String name, Consumer<TableBuilder> builder) {
		table(name, TableQueryAction.CREATE, builder, false);
	}

	public void createTableIfNotExists(Class<? extends Model<?>> modelClass, Consumer<TableBuilder> builder) {
		table(Model.getTableName(modelClass), TableQueryAction.CREATE, builder, true);
	}

	public void createTableIfNotExists(String name, Consumer<TableBuilder> builder) {
		table(name, TableQueryAction.CREATE, builder, true);
	}

	public void table(String name, Consumer<AlterTableBuilder> builder) {
		final AlterTableBuilder alterTable = new AlterTableBuilder(name, sqlDialect);
		builder.accept(alterTable);
		alterTables.add(alterTable);
	}

	public void table(Class<? extends Model<?>> modelClass, Consumer<AlterTableBuilder> builder) {
		table(Model.getTableName(modelClass), builder);
	}

	public void dropTable(Class<? extends Model<?>> modelClass) {
		dropTable(Model.getTableName(modelClass));
	}

	public void dropTable(String name) {
		tablesToDrop.add(name);
	}


	public List<Query<?>> build() {
		final List<Query<?>> queries = new ArrayList<>();

		for (final String table : tablesToDrop) {
			queries.add(new SimpleQuery("DROP TABLE " + table + ';', List.of()));
		}

		for (final TableBuilder tableBuilder : tableBuilders) {
			queries.add(new SimpleQuery(buildCreateSql(tableBuilder.build()), List.of()));
		}

		for (final AlterTableBuilder alterTable : alterTables) {
			queries.addAll(alterTable.build());
		}

		return queries;
	}


	private void table(String name, TableQueryAction action, Consumer<TableBuilder> builder, boolean ifNotExists) {
		final TableBuilder tableBuilder = new TableBuilder(name, action, sqlDialect);
		tableBuilder.ifNotExists(ifNotExists);
		builder.accept(tableBuilder);
		tableBuilders.add(tableBuilder);
	}

	private String buildCreateSql(Table table) {
		final String columns = table.getColumns()
			.stream()
			.map(column -> column.toSql(sqlDialect))
			.collect(Collectors.joining(", "));

		final StringBuilder sql = new StringBuilder("CREATE TABLE ");

		if (table.isIfNotExists()) {
			sql.append("IF NOT EXISTS ");
		}

		return sql.append(table.getName())
			.append(" (")
			.append(columns)
			.append(");")
			.toString();
	}
}
