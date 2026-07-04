package dev.czescjestemadam.database.query.builder.condition;

import dev.czescjestemadam.database.query.builder.condition.compare.QueryInCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryNullCompareCondition;
import dev.czescjestemadam.database.query.builder.condition.compare.QueryValueCompareCondition;

import java.util.Collection;
import java.util.List;

public interface QueryConditionBuilder<T> {
	T where(QueryCondition condition);

	default T where(String column, String comparator, Object value) {
		return where(QueryValueCompareCondition.and(column, comparator, value));
	}

	default T orWhere(String column, String comparator, Object value) {
		return where(QueryValueCompareCondition.or(column, comparator, value));
	}

	default T whereEquals(String column, Object value) {
		return where(column, "=", value);
	}

	default T orWhereEquals(String column, Object value) {
		return orWhere(column, "=", value);
	}

	default T whereNot(String column, Object value) {
		return where(column, "<>", value);
	}

	default T orWhereNot(String column, Object value) {
		return orWhere(column, "<>", value);
	}

	default T whereNull(String column) {
		return where(QueryNullCompareCondition.andNull(column));
	}

	default T orWhereNull(String column) {
		return where(QueryNullCompareCondition.orNull(column));
	}

	default T whereNotNull(String column) {
		return where(QueryNullCompareCondition.andNotNull(column));
	}

	default T orWhereNotNull(String column) {
		return where(QueryNullCompareCondition.orNotNull(column));
	}

	default T whereIn(String column, Collection<?> values) {
		return where(QueryInCondition.andIn(column, List.copyOf(values)));
	}

	default T whereIn(String column, Object... values) {
		return whereIn(column, List.of(values));
	}

	default T whereNotIn(String column, Collection<?> values) {
		return where(QueryInCondition.andNotIn(column, List.copyOf(values)));
	}

	default T whereNotIn(String column, Object... values) {
		return whereNotIn(column, List.of(values));
	}

	default T orWhereIn(String column, Collection<?> values) {
		return where(QueryInCondition.orIn(column, List.copyOf(values)));
	}

	default T orWhereIn(String column, Object... values) {
		return orWhereIn(column, List.of(values));
	}

	default T orWhereNotIn(String column, Collection<?> values) {
		return where(QueryInCondition.orNotIn(column, List.copyOf(values)));
	}

	default T orWhereNotIn(String column, Object... values) {
		return orWhereNotIn(column, List.of(values));
	}
}
