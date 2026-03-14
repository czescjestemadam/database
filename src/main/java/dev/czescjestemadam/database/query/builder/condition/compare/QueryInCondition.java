package dev.czescjestemadam.database.query.builder.condition.compare;

import dev.czescjestemadam.database.query.builder.condition.QueryConditionJoinType;

import java.util.List;

public class QueryInCondition extends QueryCompareCondition {
	private final List<Object> values;

	public QueryInCondition(
			QueryConditionJoinType joinType,
			boolean inverted,
			String column,
			List<Object> values
	) {
		super(joinType, inverted, column);
		this.values = values;
	}

	public List<Object> getValues() {
		return values;
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