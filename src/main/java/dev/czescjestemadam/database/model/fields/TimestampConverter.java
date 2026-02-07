package dev.czescjestemadam.database.model.fields;

import java.sql.Timestamp;
import java.time.Instant;

public class TimestampConverter implements FieldDataConverter {
	@Override
	public Object fromDatabase(Object data) {
		if (data instanceof Long value) {
			return new Timestamp(value);
		} else if (data instanceof Instant instant) {
			return Timestamp.from(instant);
		} else {
			return data;
		}
	}
}
