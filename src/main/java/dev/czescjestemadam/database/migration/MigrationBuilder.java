package dev.czescjestemadam.database.migration;

import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.migration.column.Column;
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
	private final Set<String> tablesToDrop = new HashSet<>();
	private final SqlDialect sqlDialect;

	public MigrationBuilder(SqlDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	public void createTable(Class<? extends Model<?>> modelClass, Consumer<TableBuilder> builder) {
		table(Model.getTableName(modelClass), TableQueryAction.CREATE, builder);
	}

	public void createTable(String name, Consumer<TableBuilder> builder) {
		table(name, TableQueryAction.CREATE, builder);
	}

	// TODO
//	public void table(Class<? extends Model> modelClass, Consumer<TableBuilder> builder) {
//		table(Model.getTableName(modelClass), builder, TableQueryAction.ALTER);
//	}
//
//	public void table(String name, Consumer<TableBuilder> builder) {
//		table(name, builder, TableQueryAction.ALTER);
//	}

	public void dropTable(Class<? extends Model<?>> modelClass) {
		dropTable(Model.getTableName(modelClass));
	}

	public void dropTable(String name) {
		tablesToDrop.add(name);
	}


	public Query<?> build() {
		final StringBuilder sql = new StringBuilder();

		if (!tablesToDrop.isEmpty()) {
			for (final String table : tablesToDrop) {
				sql.append("DROP TABLE ").append(table).append(';');
			}
		}

		for (final TableBuilder tableBuilder : tableBuilders) {
			final Table table = tableBuilder.build();

			sql.append("CREATE TABLE IF NOT EXISTS ")
					.append(table.getName())
					.append(" (")
					.append(table.getColumns()
							.stream()
							.map(this::buildColumnSql)
							.collect(Collectors.joining(", ")))
					.append(");");
		}

		return new SimpleQuery(sql.toString(), List.of());
	}


	private void table(String name, TableQueryAction action, Consumer<TableBuilder> builder) {
		final TableBuilder tableBuilder = new TableBuilder(name, action);
		builder.accept(tableBuilder);
		tableBuilders.add(tableBuilder);
	}

	private String buildColumnSql(Column column) {
		final StringBuilder sql = new StringBuilder();

		sql.append(column.getName()).append(' ').append(column.getType());

		if (column.getSize() > 0) {
			sql.append('(').append(column.getSize()).append(')');
		}

		if (!column.isNullable()) {
			sql.append(" NOT NULL");
		}

		if (column.getDefaultValue() != null) {
			sql.append(" DEFAULT ").append(column.getDefaultValue());
		}

		if (column.isUnique()) {
			sql.append(" UNIQUE");
		}

		if (column.isPrimaryKey()) {
			sql.append(" PRIMARY KEY");
		}

		if (column.isAutoIncrement()) {
			sql.append(' ').append(sqlDialect.getAutoIncrement());
		}

		return sql.toString();
	}
}
