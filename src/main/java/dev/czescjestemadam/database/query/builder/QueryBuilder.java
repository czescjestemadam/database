package dev.czescjestemadam.database.query.builder;


import dev.czescjestemadam.database.exceptions.query.QueryException;
import dev.czescjestemadam.database.query.OrderType;
import dev.czescjestemadam.database.query.builder.condition.QueryCondition;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionBuilder;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionGroup;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryCompareCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryNullCompareCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryValueCompareCondition;
import dev.czescjestemadam.database.query.impl.SelectQuery;
import dev.czescjestemadam.database.query.impl.UpdateQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryBuilder implements QueryConditionBuilder<QueryBuilder>, QueryOrderBuilder<QueryBuilder> {
	private final String table;
	private final Set<String> columns;

	private final List<QueryCondition> conditions = new ArrayList<>();
	private final List<QueryOrder> orders = new ArrayList<>();

	private int limit = -1;
	private int offset = -1;

	public QueryBuilder(String table, Set<String> columns) {
		this.table = table;
		this.columns = columns;
	}

	@Override
	public QueryBuilder where(QueryCondition condition) {
		conditions.add(condition);
		return this;
	}

	@Override
	public QueryBuilder orderBy(String column, OrderType type) {
		orders.add(new QueryOrder(column, type));
		return this;
	}

	public QueryBuilder limit(int limit) {
		if (limit < 1) {
			throw new IllegalArgumentException("Limit cannot be less than 1");
		}

		this.limit = limit;
		return this;
	}

	public QueryBuilder withoutLimit() {
		this.limit = -1;
		return this;
	}

	public QueryBuilder offset(int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset cannot be less than 0");
		}

		this.offset = offset;
		return this;
	}

	public QueryBuilder withoutOffset() {
		this.offset = -1;
		return this;
	}


	public SelectQuery select() {
		final StringBuilder sql = new StringBuilder();
		final List<Object> parameters = new ArrayList<>();

		sql.append("SELECT ")
			.append(buildColumns())
			.append(" FROM ")
			.append(table);

		appendConditions(sql, parameters);

		if (!orders.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(buildOrders());
		}

		appendLimitOffset(sql);

		sql.append(';');

		return new SelectQuery(sql.toString(), parameters);
	}

	public UpdateQuery update(Object... updates) {
		if (updates.length % 2 != 0) {
			throw new IllegalArgumentException("Argument count must be even");
		}

		final List<Map.Entry<String, Object>> args = new ArrayList<>();

		String column = null;

		for (int i = 0; i < updates.length; i++) {
			final Object arg = updates[i];

			if (i % 2 == 0) {
				column = String.valueOf(arg);
			} else {
				args.add(Map.entry(column, arg));
			}
		}

		return update(args);
	}

	@SafeVarargs
	public final UpdateQuery update(Map.Entry<String, Object>... updates) {
		return update(List.of(updates));
	}

	public UpdateQuery update(List<Map.Entry<String, Object>> updates) {
		if (updates.isEmpty()) {
			throw new QueryException("Empty update values");
		}

		final StringBuilder sql = new StringBuilder();
		final List<Object> parameters = new ArrayList<>();

		sql.append("UPDATE ")
			.append(table)
			.append(" SET ");

		for (int i = 0; i < updates.size(); i++) {
			final Map.Entry<String, Object> entry = updates.get(i);

			if (i > 0) {
				sql.append(", ");
			}

			sql.append(entry.getKey())
				.append(" = ?");

			parameters.add(entry.getValue());
		}

		appendConditions(sql, parameters);

		if (!orders.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(buildOrders());
		}

		appendLimitOffset(sql);

		return new UpdateQuery(sql.toString(), parameters, false);
	}

	public UpdateQuery delete() {
		final StringBuilder sql = new StringBuilder();
		final List<Object> parameters = new ArrayList<>();

		sql.append("DELETE FROM ")
			.append(table);

		appendConditions(sql, parameters);

		if (!orders.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(buildOrders());
		}

		appendLimitOffset(sql);

		return new UpdateQuery(sql.toString(), parameters, false);
	}


	private String buildColumns() {
		return columns.isEmpty() ? "*" : String.join(", ", columns);
	}

	private void appendConditions(StringBuilder sql, List<Object> parameters) {
		if (conditions.isEmpty()) {
			return;
		}

		sql.append(" WHERE ");
		buildConditions(conditions, sql, parameters);
	}

	private void appendLimitOffset(StringBuilder sql) {
		if (limit > 0) {
			sql.append(" LIMIT ")
				.append(limit);

			if (offset >= 0) {
				sql.append(" OFFSET ")
					.append(offset);
			}
		}
	}

	private static void buildConditions(
		List<QueryCondition> conditions,
		StringBuilder sql,
		List<Object> parameters
	) {
		for (int i = 0; i < conditions.size(); i++) {
			final QueryCondition condition = conditions.get(i);

			if (i > 0) {
				sql.append(' ')
					.append(condition.getJoinType())
					.append(' ');
			}

			if (condition instanceof QueryConditionGroup group) {
				sql.append('(');
				buildConditions(group.getConditions(), sql, parameters);
				sql.append(')');
			} else if (condition instanceof QueryCompareCondition compareCondition) {
				buildCompareCondition(compareCondition, sql, parameters);
			} else {
				throw new QueryException("Unexpected QueryCondition instance: " + condition);
			}
		}
	}

	private static void buildCompareCondition(
		QueryCompareCondition condition,
		StringBuilder sql,
		List<Object> parameters
	) {
		sql.append(condition.getColumn())
			.append(' ');

		if (condition instanceof QueryValueCompareCondition valueCompareCondition) {
			sql.append(valueCompareCondition.getComparator())
				.append(" ?");

			parameters.add(valueCompareCondition.getValue());
		} else if (condition instanceof QueryNullCompareCondition nullCompareCondition) {
			sql.append(
				nullCompareCondition.isInverted() ?
					"IS NOT NULL" :
					"IS NULL"
			);
		} else {
			throw new QueryException("Unexpected QueryCompareCondition instance: " + condition);
		}
	}

	private String buildOrders() {
		return orders.stream()
			.map(QueryOrder::toString)
			.collect(Collectors.joining(", "));
	}
}
