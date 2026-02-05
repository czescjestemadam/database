package dev.czescjestemadam.database.query.builder;

import dev.czescjestemadam.database.exceptions.query.QueryException;
import dev.czescjestemadam.database.query.impl.UpdateQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InsertQueryBuilder {
	private final String table;
	private final List<String> columns = new ArrayList<>();
	private final List<List<Object>> valuesList = new ArrayList<>();

	public InsertQueryBuilder(String table) {
		this.table = table;
	}

	public InsertQueryBuilder columns(String... columns) {
		return columns(List.of(columns));
	}

	public InsertQueryBuilder columns(Collection<String> columns) {
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("Columns cannot be empty");
		}

		this.columns.addAll(columns);
		return this;
	}

	public InsertQueryBuilder values(Object... values) {
		return values(List.of(values));
	}

	public InsertQueryBuilder values(List<Object> values) {
		if (values.isEmpty()) {
			throw new IllegalArgumentException("Values cannot be empty");
		}

		if (!columns.isEmpty() && columns.size() != values.size()) {
			throw new IllegalArgumentException(String.format(
					"Size of values (%d) does not match size of columns (%d)",
					values.size(),
					columns.size()
			));
		} else if (!this.valuesList.isEmpty() && this.valuesList.getFirst().size() != values.size()) {
			throw new IllegalArgumentException(String.format(
					"Size of supplied values (%d) does not match size of other values (%d)",
					values.size(),
					this.valuesList.getFirst().size()
			));
		}

		this.valuesList.add(values);
		return this;
	}

	public UpdateQuery build() {
		return build(false);
	}

	public UpdateQuery build(boolean returnGeneratedKeys) {
		if (valuesList.isEmpty()) {
			throw new QueryException("Nothing to insert, values are empty");
		}

		final StringBuilder sql = new StringBuilder();
		final List<Object> parameters = new ArrayList<>();

		sql.append("INSERT INTO ")
				.append(table);

		if (!columns.isEmpty()) {
			sql.append(" (")
					.append(String.join(", ", columns))
					.append(')');
		}

		sql.append(" VALUES ");

		for (int i = 0; i < valuesList.size(); i++) {
			final List<Object> values = valuesList.get(i);

			if (i > 0) {
				sql.append(", ");
			}

			sql.append('(');

			for (int j = 0; j < values.size(); j++) {
				if (j > 0) {
					sql.append(", ");
				}

				sql.append('?');
				parameters.add(values.get(j));
			}

			sql.append(')');
		}

		return new UpdateQuery(sql.toString(), parameters, returnGeneratedKeys);
	}
}
