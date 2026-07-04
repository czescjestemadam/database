package dev.czescjestemadam.database.query.builder;

import dev.czescjestemadam.database.query.OrderType;

public class QueryOrder {
	private final String column;
	private final OrderType type;

	public QueryOrder(String column, OrderType type) {
		this.column = column;
		this.type = type;
	}

	public String getColumn() {
		return column;
	}

	public OrderType getType() {
		return type;
	}

	public String toSql() {
		return column + ' ' + type;
	}

	@Override
	public String toString() {
		return toSql();
	}
}
