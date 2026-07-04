package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

import java.util.List;

public class QueryValueCompareCondition extends QueryCompareCondition {
	private final String comparator;
	private final Object value;

	public QueryValueCompareCondition(
		QueryConditionJoinType joinType,
		String column,
		String comparator,
		Object value
	) {
		super(joinType, column);
		this.comparator = comparator;
		this.value = value;
	}

	public static QueryValueCompareCondition and(String column, String comparator, Object value) {
		return new QueryValueCompareCondition(QueryConditionJoinType.AND, column, comparator, value);
	}

	public static QueryValueCompareCondition or(String column, String comparator, Object value) {
		return new QueryValueCompareCondition(QueryConditionJoinType.OR, column, comparator, value);
	}

	@Override
	public void appendTo(StringBuilder sql, List<Object> parameters) {
		sql.append(column)
			.append(' ')
			.append(comparator)
			.append(" ?");

		parameters.add(value);
	}

	@Override
	public String toString() {
		return "QueryValueCompareCondition{" +
		       "comparator='" + comparator + '\'' +
		       ", value=" + value +
		       ", joinType=" + joinType +
		       ", column='" + column + '\'' +
		       '}';
	}
}
