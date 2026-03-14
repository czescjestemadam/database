package dev.czescjestemadam.database.model.fields;

import java.math.BigInteger;

public class BigIntegerConverter implements FieldDataConverter {
	@Override
	public Object fromDatabase(Object data) {
		return switch (data) {
			case null -> null;
			case BigInteger bigInteger -> data;
			case Integer i -> BigInteger.valueOf(i);
			case Long l -> BigInteger.valueOf(l);
			case Number number -> BigInteger.valueOf(number.longValue());
			default -> throw new IllegalArgumentException("Cannot convert " + data.getClass() + " to BigInteger");
		};
	}
}
