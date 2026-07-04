package dev.czescjestemadam.database.query.builder.condition.compare;


import dev.czescjestemadam.database.query.builder.condition.QueryCondition;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

public abstract class QueryCompareCondition implements QueryCondition {
	protected final QueryConditionJoinType joinType;
	protected final String column;

	public QueryCompareCondition(QueryConditionJoinType joinType, String column) {
		this.joinType = joinType;
		this.column = column;
	}

	@Override
	public QueryConditionJoinType getJoinType() {
		return joinType;
	}

	public String getColumn() {
		return column;
	}
}
