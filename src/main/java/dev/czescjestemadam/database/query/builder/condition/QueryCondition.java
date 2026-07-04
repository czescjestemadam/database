package dev.czescjestemadam.database.query.builder.condition;

import java.util.List;

public interface QueryCondition {
	QueryConditionJoinType getJoinType();

	void appendTo(StringBuilder sql, List<Object> parameters);

	static void appendList(List<QueryCondition> conditions, StringBuilder sql, List<Object> parameters) {
		for (int i = 0; i < conditions.size(); i++) {
			final QueryCondition condition = conditions.get(i);

			if (i > 0) {
				sql.append(' ')
					.append(condition.getJoinType())
					.append(' ');
			}

			condition.appendTo(sql, parameters);
		}
	}
}