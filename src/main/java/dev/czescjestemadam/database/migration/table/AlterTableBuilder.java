package dev.czescjestemadam.database.migration.table;

import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.migration.column.ColumnBuilder;
import dev.czescjestemadam.database.migration.column.ColumnType;
import dev.czescjestemadam.database.query.impl.SimpleQuery;

import java.util.ArrayList;
import java.util.List;

public class AlterTableBuilder {
	private final String table;
	private final SqlDialect dialect;
	private final List<AlterAction> actions = new ArrayList<>();

	public AlterTableBuilder(String table, SqlDialect dialect) {
		this.table = table;
		this.dialect = dialect;
	}

	public ColumnBuilder addColumn(ColumnType type, String name) {
		return addColumn(type, -1, name);
	}

	public ColumnBuilder addColumn(ColumnType type, int size, String name) {
		final ColumnBuilder builder = new ColumnBuilder(type, size, name);
		actions.add(new AddColumnAction(builder));
		return builder;
	}

	public ColumnBuilder string(String name) {
		return addColumn(ColumnType.VARCHAR, name);
	}

	public ColumnBuilder text(String name) {
		return addColumn(ColumnType.TEXT, name);
	}

	public ColumnBuilder integer(String name) {
		return addColumn(ColumnType.INTEGER, name);
	}

	public ColumnBuilder bigInt(String name) {
		return addColumn(ColumnType.BIGINT, name);
	}

	public ColumnBuilder bool(String name) {
		return addColumn(ColumnType.BOOL, name);
	}

	public ColumnBuilder timestamp(String name) {
		return addColumn(ColumnType.TIMESTAMP, name);
	}

	public AlterTableBuilder dropColumn(String name) {
		actions.add(new DropColumnAction(name));
		return this;
	}

	public AlterTableBuilder renameColumn(String from, String to) {
		actions.add(new RenameColumnAction(from, to));
		return this;
	}

	public AlterTableBuilder rename(String newName) {
		actions.add(new RenameTableAction(newName));
		return this;
	}

	public List<SimpleQuery> build() {
		return actions.stream()
			.map(action -> action.toQuery(table, dialect))
			.toList();
	}


	private sealed interface AlterAction
		permits AddColumnAction, DropColumnAction, RenameColumnAction, RenameTableAction {
		SimpleQuery toQuery(String table, SqlDialect dialect);
	}

	private record AddColumnAction(ColumnBuilder column) implements AlterAction {
		@Override
		public SimpleQuery toQuery(String table, SqlDialect dialect) {
			final String sql = "ALTER TABLE " + table + " ADD COLUMN " + column.build().toSql(dialect) + ';';

			return new SimpleQuery(sql, List.of());
		}
	}

	private record DropColumnAction(String column) implements AlterAction {
		@Override
		public SimpleQuery toQuery(String table, SqlDialect dialect) {
			return new SimpleQuery("ALTER TABLE " + table + " DROP COLUMN " + column + ';', List.of());
		}
	}

	private record RenameColumnAction(String from, String to) implements AlterAction {
		@Override
		public SimpleQuery toQuery(String table, SqlDialect dialect) {
			return new SimpleQuery("ALTER TABLE " + table + " RENAME COLUMN " + from + " TO " + to + ';', List.of());
		}
	}

	private record RenameTableAction(String newName) implements AlterAction {
		@Override
		public SimpleQuery toQuery(String table, SqlDialect dialect) {
			return new SimpleQuery("ALTER TABLE " + table + " RENAME TO " + newName + ';', List.of());
		}
	}
}
