package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

import java.util.Collections;
import java.util.List;

public class QueryInCondition extends QueryCompareCondition {
	private final boolean inverted;
	private final List<Object> values;

	public QueryInCondition(
		QueryConditionJoinType joinType,
		boolean inverted,
		String column,
		List<Object> values
	) {
		super(joinType, column);
		this.inverted = inverted;
		this.values = values;
	}

	public static QueryInCondition andIn(String column, List<Object> values) {
		return new QueryInCondition(QueryConditionJoinType.AND, false, column, values);
	}

	public static QueryInCondition orIn(String column, List<Object> values) {
		return new QueryInCondition(QueryConditionJoinType.OR, false, column, values);
	}

	public static QueryInCondition andNotIn(String column, List<Object> values) {
		return new QueryInCondition(QueryConditionJoinType.AND, true, column, values);
	}

	public static QueryInCondition orNotIn(String column, List<Object> values) {
		return new QueryInCondition(QueryConditionJoinType.OR, true, column, values);
	}

	@Override
	public void appendTo(StringBuilder sql, List<Object> parameters) {
		if (values.isEmpty()) {
			sql.append(inverted ? "1 = 1" : "0 = 1");
			return;
		}

		sql.append(column);

		if (inverted) {
			sql.append(" NOT");
		}

		sql.append(" IN (")
			.append(String.join(", ", Collections.nCopies(values.size(), "?")))
			.append(')');

		parameters.addAll(values);
	}

	@Override
	public String toString() {
		return "QueryInCondition{" +
		       "values=" + values +
		       ", joinType=" + joinType +
		       ", inverted=" + inverted +
		       ", column='" + column + '\'' +
		       '}';
	}
}
