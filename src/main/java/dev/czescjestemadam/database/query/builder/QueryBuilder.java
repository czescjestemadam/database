package dev.czescjestemadam.database.query.builder;


import dev.czescjestemadam.database.exceptions.query.QueryException;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.query.OrderType;
import dev.czescjestemadam.database.query.builder.condition.QueryCondition;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionBuilder;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionGroup;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;
import dev.czescjestemadam.database.query.impl.SelectQuery;
import dev.czescjestemadam.database.query.impl.UpdateQuery;
import dev.czescjestemadam.database.repository.Repository;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class QueryBuilder implements QueryConditionBuilder<QueryBuilder>, QueryOrderBuilder<QueryBuilder> {
	private final String table;
	private final List<String> columns;

	private final List<QueryCondition> conditions = new ArrayList<>();
	private final List<QueryOrder> orders = new ArrayList<>();

	private int limit = -1;
	private int offset = -1;

	@Nullable
	private final Repository<?> repository;

	public QueryBuilder(String table, List<String> columns) {
		this(table, columns, null);
	}

	public QueryBuilder(String table, List<String> columns, @Nullable Repository<?> repository) {
		this.table = table;
		this.columns = columns;
		this.repository = repository;
	}

	private QueryBuilder(QueryBuilder other) {
		this.table = other.table;
		this.columns = new ArrayList<>(other.columns);
		this.conditions.addAll(other.conditions);
		this.orders.addAll(other.orders);
		this.limit = other.limit;
		this.offset = other.offset;
		this.repository = other.repository;
	}

	public QueryBuilder copy() {
		return new QueryBuilder(this);
	}

	@Override
	public QueryBuilder where(QueryCondition condition) {
		conditions.add(condition);
		return this;
	}

	public QueryBuilder where(Consumer<QueryBuilder> group) {
		conditions.add(groupCondition(QueryConditionJoinType.AND, group));
		return this;
	}

	public QueryBuilder orWhere(Consumer<QueryBuilder> group) {
		conditions.add(groupCondition(QueryConditionJoinType.OR, group));
		return this;
	}

	private QueryConditionGroup groupCondition(QueryConditionJoinType joinType, Consumer<QueryBuilder> group) {
		final QueryBuilder sub = new QueryBuilder(table, List.of());

		group.accept(sub);

		return new QueryConditionGroup(joinType, sub.conditions);
	}

	@Override
	public QueryBuilder orderBy(String column, OrderType type) {
		orders.add(new QueryOrder(column, type));
		return this;
	}

	public QueryBuilder limit(@Range(from = 1, to = Integer.MAX_VALUE) int limit) {
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

	public QueryBuilder offset(@Range(from = 0, to = Integer.MAX_VALUE) int offset) {
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

	public SelectQuery countQuery() {
		final StringBuilder sql = new StringBuilder();
		final List<Object> parameters = new ArrayList<>();

		sql.append("SELECT COUNT(*) FROM ")
			.append(table);

		appendConditions(sql, parameters);

		sql.append(';');

		return new SelectQuery(sql.toString(), parameters);
	}

	public UpdateQuery update(Object... updates) {
		if (updates.length % 2 != 0) {
			throw new IllegalArgumentException("Argument count must be even");
		}

		final List<Map.Entry<String, Object>> args = new ArrayList<>();

		for (int i = 0; i < updates.length; i += 2) {
			args.add(Map.entry(String.valueOf(updates[i]), updates[i + 1]));
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

	public <T extends Model<T>> T first() {
		@SuppressWarnings("unchecked") final Repository<T> repo = (Repository<T>)requireRepository();

		return repo.first(this);
	}

	public <T extends Model<T>> T firstOrFail() {
		@SuppressWarnings("unchecked") final Repository<T> repo = (Repository<T>)requireRepository();

		return repo.firstOrFail(this);
	}

	private Repository<?> requireRepository() {
		if (repository == null) {
			throw new QueryException(
				"Cannot call terminal query methods without a repository. " +
				"Create the QueryBuilder via repository.query()"
			);
		}

		return repository;
	}

	private String buildColumns() {
		return columns.isEmpty() ? "*" : String.join(", ", columns);
	}

	private void appendConditions(StringBuilder sql, List<Object> parameters) {
		if (conditions.isEmpty()) {
			return;
		}

		sql.append(" WHERE ");
		QueryCondition.appendList(conditions, sql, parameters);
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

	private String buildOrders() {
		final StringBuilder sql = new StringBuilder();

		for (int i = 0; i < orders.size(); i++) {
			if (i > 0) {
				sql.append(", ");
			}

			sql.append(orders.get(i).toSql());
		}

		return sql.toString();
	}
}
