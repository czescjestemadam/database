package dev.czescjestemadam.database.migration.table;

import dev.czescjestemadam.database.dialect.SqlDialect;
import dev.czescjestemadam.database.migration.column.ColumnBuilder;
import dev.czescjestemadam.database.migration.column.ColumnType;
import dev.czescjestemadam.database.query.TableQueryAction;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {
	private final String name;
	private final TableQueryAction action;
	private final SqlDialect sqlDialect;
	private final List<ColumnBuilder> columnBuilders = new ArrayList<>();
	private boolean ifNotExists;

	public TableBuilder(String name, TableQueryAction action, SqlDialect sqlDialect) {
		this.name = name;
		this.action = action;
		this.sqlDialect = sqlDialect;
	}

	// numeric

	public void id() {
		column(sqlDialect == SqlDialect.SQLITE ? ColumnType.INTEGER : ColumnType.BIGINT, "id")
			.primaryKey()
			.autoIncrement();
	}

	// TODO: foreign id

	public ColumnBuilder bool(String name) {
		return column(ColumnType.BOOL, name);
	}

	public ColumnBuilder tinyInt(String name) {
		return column(ColumnType.TINYINT, name);
	}

	public ColumnBuilder smallInt(String name) {
		return column(ColumnType.SMALLINT, name);
	}

	public ColumnBuilder mediumInt(String name) {
		return column(ColumnType.MEDIUMINT, name);
	}

	public ColumnBuilder integer(String name) {
		return column(ColumnType.INTEGER, name);
	}

	public ColumnBuilder bigInt(String name) {
		return column(ColumnType.BIGINT, name);
	}

	public ColumnBuilder float_(String name) {
		return column(ColumnType.FLOAT, name);
	}

	public ColumnBuilder double_(String name) {
		return column(ColumnType.DOUBLE, name);
	}

	// string

	public ColumnBuilder character(String name) {
		return column(ColumnType.CHAR, name);
	}

	public ColumnBuilder character(int size, String name) {
		return column(ColumnType.CHAR, size, name);
	}

	/** alias for {@link TableBuilder#varchar(String)} */
	public ColumnBuilder string(String name) {
		return varchar(name);
	}

	/** alias for {@link TableBuilder#varchar(int, String)} */
	public ColumnBuilder string(int size, String name) {
		return varchar(size, name);
	}

	public ColumnBuilder varchar(String name) {
		return column(ColumnType.VARCHAR, name);
	}

	public ColumnBuilder varchar(int size, String name) {
		return column(ColumnType.VARCHAR, size, name);
	}

	public ColumnBuilder text(int size, String name) {
		return column(ColumnType.TEXT, size, name);
	}

	public ColumnBuilder text(String name) {
		return column(ColumnType.TEXT, name);
	}

	public ColumnBuilder mediumText(int size, String name) {
		return column(ColumnType.MEDIUMTEXT, size, name);
	}

	public ColumnBuilder mediumText(String name) {
		return column(ColumnType.MEDIUMTEXT, name);
	}

	public ColumnBuilder longText(int size, String name) {
		return column(ColumnType.LONGTEXT, size, name);
	}

	public ColumnBuilder longText(String name) {
		return column(ColumnType.LONGTEXT, name);
	}

	public ColumnBuilder tinyBlob(int size, String name) {
		return column(ColumnType.TINYBLOB, size, name);
	}

	public ColumnBuilder tinyBlob(String name) {
		return column(ColumnType.TINYBLOB, name);
	}

	public ColumnBuilder blob(int size, String name) {
		return column(ColumnType.BLOB, size, name);
	}

	public ColumnBuilder blob(String name) {
		return column(ColumnType.BLOB, name);
	}

	public ColumnBuilder longBlob(int size, String name) {
		return column(ColumnType.LONGBLOB, size, name);
	}

	public ColumnBuilder longBlob(String name) {
		return column(ColumnType.LONGBLOB, name);
	}

	// date / time

	public ColumnBuilder date(String name) {
		return column(ColumnType.DATE, name);
	}

	public ColumnBuilder timestamp(String name) {
		return column(ColumnType.TIMESTAMP, name);
	}

	public ColumnBuilder time(String name) {
		return column(ColumnType.TIME, name);
	}

	public ColumnBuilder year(String name) {
		return column(ColumnType.YEAR, name);
	}

	// other

	public ColumnBuilder column(ColumnType type, String name) {
		return column(type, -1, name);
	}

	public ColumnBuilder column(ColumnType type, int size, String name) {
		final ColumnBuilder builder = new ColumnBuilder(type, size, name);
		columnBuilders.add(builder);
		return builder;
	}

	public TableBuilder ifNotExists() {
		return ifNotExists(true);
	}

	public TableBuilder ifNotExists(boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
		return this;
	}


	public Table build() {
		return new Table(
			name,
			action,
			columnBuilders.stream()
				.map(ColumnBuilder::build)
				.toList(),
			ifNotExists
		);
	}
}
