package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

import java.util.List;

public class QueryNullCompareCondition extends QueryCompareCondition {
	private final boolean inverted;

	public QueryNullCompareCondition(QueryConditionJoinType joinType, boolean inverted, String column) {
		super(joinType, column);
		this.inverted = inverted;
	}

	public static QueryNullCompareCondition andNull(String column) {
		return new QueryNullCompareCondition(QueryConditionJoinType.AND, false, column);
	}

	public static QueryNullCompareCondition orNull(String column) {
		return new QueryNullCompareCondition(QueryConditionJoinType.OR, false, column);
	}

	public static QueryNullCompareCondition andNotNull(String column) {
		return new QueryNullCompareCondition(QueryConditionJoinType.AND, true, column);
	}

	public static QueryNullCompareCondition orNotNull(String column) {
		return new QueryNullCompareCondition(QueryConditionJoinType.OR, true, column);
	}

	@Override
	public void appendTo(StringBuilder sql, List<Object> parameters) {
		sql.append(column)
			.append(inverted ? " IS NOT NULL" : " IS NULL");
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
