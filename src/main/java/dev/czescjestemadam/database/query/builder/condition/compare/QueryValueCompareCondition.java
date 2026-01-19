package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

public class QueryValueCompareCondition extends QueryCompareCondition {
	private final String comparator;
	private final Object value;

	public QueryValueCompareCondition(
			QueryConditionJoinType joinType,
			boolean inverted,
			String column,
			String comparator,
			Object value
	) {
		super(joinType, inverted, column);
		this.comparator = comparator;
		this.value = value;
	}

	public String getComparator() {
		return comparator;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "QueryValueCompareCondition{" +
				"comparator='" + comparator + '\'' +
				", value=" + value +
				", joinType=" + joinType +
				", inverted=" + inverted +
				", column='" + column + '\'' +
				'}';
	}
}
