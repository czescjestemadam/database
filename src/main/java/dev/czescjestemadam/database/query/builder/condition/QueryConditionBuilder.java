package dev.czescjestemadam.database.query.builder.condition;

import dev.czescjestemadam.database.query.builder.condition.compare.QueryNullCompareCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryValueCompareCondition;

public interface QueryConditionBuilder<T> {
	T where(QueryCondition condition);

	default T where(String column, String comparator, Object value) {
		return where(new QueryValueCompareCondition(
			QueryConditionJoinType.AND,
			false,
			column,
			comparator,
			value
		));
	}

	default T orWhere(String column, String comparator, Object value) {
		return where(new QueryValueCompareCondition(
			QueryConditionJoinType.OR,
			false,
			column,
			comparator,
			value
		));
	}

	default T whereEquals(String column, Object value) {
		return where(column, "=", value);
	}

	default T orWhereEquals(String column, Object value) {
		return orWhere(column, "=", value);
	}

	default T whereNot(String column, Object value) {
		return where(new QueryValueCompareCondition(
			QueryConditionJoinType.AND,
			true,
			column,
			"=",
			value
		));
	}

	default T orWhereNot(String column, Object value) {
		return where(new QueryValueCompareCondition(
			QueryConditionJoinType.OR,
			true,
			column,
			"=",
			value
		));
	}

	default T whereNull(String column) {
		return where(new QueryNullCompareCondition(
			QueryConditionJoinType.AND,
			false,
			column
		));
	}

	default T orWhereNull(String column) {
		return where(new QueryNullCompareCondition(
			QueryConditionJoinType.OR,
			false,
			column
		));
	}

	default T whereNotNull(String column) {
		return where(new QueryNullCompareCondition(
			QueryConditionJoinType.AND,
			true,
			column
		));
	}

	default T orWhereNotNull(String column) {
		return where(new QueryNullCompareCondition(
			QueryConditionJoinType.OR,
			true,
			column
		));
	}
}
