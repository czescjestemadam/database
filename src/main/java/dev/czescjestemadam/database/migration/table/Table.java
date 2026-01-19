package dev.czescjestemadam.database.migration.table;

import dev.czescjestemadam.database.migration.column.Column;
import dev.czescjestemadam.database.query.TableQueryAction;

import java.util.List;

public class Table {
	private final String name;
	private final TableQueryAction action;
	private final List<Column> columns;

	public Table(String name, TableQueryAction action, List<Column> columns) {
		this.name = name;
		this.action = action;
		this.columns = columns;
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

	@Override
	public String toString() {
		return "Table{" +
				"name='" + name + '\'' +
				", action=" + action +
				", columns=" + columns +
				'}';
	}
}
