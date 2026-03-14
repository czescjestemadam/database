package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

public class QueryNullCompareCondition extends QueryCompareCondition {
	public QueryNullCompareCondition(
		QueryConditionJoinType joinType,
		boolean inverted,
		String column
	) {
		super(joinType, inverted, column);
	}

	@Override
	public String toString() {
		return "QueryNullCompareCondition{" +
			"joinType=" + joinType +
			", inverted=" + inverted +
			", column='" + column + '\'' +
			'}';
	}
}
