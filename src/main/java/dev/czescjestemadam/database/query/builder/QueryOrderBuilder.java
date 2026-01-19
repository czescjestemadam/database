package dev.czescjestemadam.database.query.builder;

import dev.czescjestemadam.database.query.OrderType;

public interface QueryOrderBuilder<T> {
	T orderBy(String column, OrderType type);

	default T orderBy(String column) {
		return orderBy(column, OrderType.ASC);
	}

	default T orderByDesc(String column) {
		return orderBy(column, OrderType.DESC);
	}
}
