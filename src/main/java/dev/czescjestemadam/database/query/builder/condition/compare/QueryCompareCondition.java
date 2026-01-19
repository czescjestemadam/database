package dev.czescjestemadam.database.query.builder.condition.compare;


import dev.czescjestemadam.database.query.builder.condition.QueryCondition;
import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

public abstract class QueryCompareCondition implements QueryCondition {
	protected final QueryConditionJoinType joinType;

	protected final boolean inverted;

	protected final String column;

	public QueryCompareCondition(QueryConditionJoinType joinType, boolean inverted, String column) {
		this.joinType = joinType;
		this.inverted = inverted;
		this.column = column;
	}

	public QueryConditionJoinType getJoinType() {
		return joinType;
	}

	public boolean isInverted() {
		return inverted;
	}

	public String getColumn() {
		return column;
	}
}
