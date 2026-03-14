package dev.czescjestemadam.database.query.builder.condition;

import java.util.List;

public class QueryConditionGroup implements QueryCondition {
	private final QueryConditionJoinType joinType;
	private final List<QueryCondition> conditions;

	public QueryConditionGroup(QueryConditionJoinType joinType, List<QueryCondition> conditions) {
		this.joinType = joinType;
		this.conditions = conditions;
	}

	@Override
	public QueryConditionJoinType getJoinType() {
		return joinType;
	}

	public List<QueryCondition> getConditions() {
		return conditions;
	}

	@Override
	public String toString() {
		return "QueryConditionGroup{" +
			"joinType=" + joinType +
			", conditions=" + conditions +
			'}';
	}
}
