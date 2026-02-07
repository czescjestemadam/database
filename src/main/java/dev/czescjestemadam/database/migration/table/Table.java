package dev.czescjestemadam.database.migration.table;

import dev.czescjestemadam.database.migration.column.Column;
import dev.czescjestemadam.database.query.TableQueryAction;

import java.util.List;

public class Table {
	private final String name;
	private final TableQueryAction action;
	private final List<Column> columns;
	private final boolean ifNotExists;

	public Table(String name, TableQueryAction action, List<Column> columns, boolean ifNotExists) {
		this.name = name;
		this.action = action;
		this.columns = columns;
		this.ifNotExists = ifNotExists;
	}

	public String getName() {
		return name;
	}

	public TableQueryAction getAction() {
		return action;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public boolean isIfNotExists() {
		return ifNotExists;
	}

	@Override
	public String toString() {
		return "Table{" +
				"name='" + name + '\'' +
				", action=" + action +
				", columns=" + columns +
				", ifNotExists=" + ifNotExists +
				'}';
	}
}
