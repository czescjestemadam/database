package dev.czescjestemadam.database.query.builder.condition;

import dev.czescjestemadam.database.query.builder.condition.compare.QueryInCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryNullCompareCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryValueCompareCondition;

import java.util.Collection;
import java.util.List;

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

	default T whereIn(String column, Collection<?> values) {
		return where(new QueryInCondition(
			QueryConditionJoinType.AND,
			false,
			column,
			List.copyOf(values)
		));
	}

	default T whereIn(String column, Object... values) {
		return whereIn(column, List.of(values));
	}

	default T whereNotIn(String column, Collection<?> values) {
		return where(new QueryInCondition(
			QueryConditionJoinType.AND,
			true,
			column,
			List.copyOf(values)
		));
	}

	default T whereNotIn(String column, Object... values) {
		return whereNotIn(column, List.of(values));
	}

	default T orWhereIn(String column, Collection<?> values) {
		return where(new QueryInCondition(
			QueryConditionJoinType.OR,
			false,
			column,
			List.copyOf(values)
		));
	}

	default T orWhereIn(String column, Object... values) {
		return orWhereIn(column, List.of(values));
	}

	default T orWhereNotIn(String column, Collection<?> values) {
		return where(new QueryInCondition(
			QueryConditionJoinType.OR,
			true,
			column,
			List.copyOf(values)
		));
	}

	default T orWhereNotIn(String column, Object... values) {
		return orWhereNotIn(column, List.of(values));
	}
}
