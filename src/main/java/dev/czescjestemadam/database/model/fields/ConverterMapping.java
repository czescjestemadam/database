package dev.czescjestemadam.database.model.fields;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConverterMapping {
	private final Map<Type, FieldDataConverter> converters = new HashMap<>();

	public ConverterMapping() {
	}

	public ConverterMapping(Map<Type, FieldDataConverter> converters) {
		this.converters.putAll(converters);
	}

	public ConverterMapping with(Type type, FieldDataConverter converter) {
		this.converters.put(type, converter);
		return this;
	}

	public Map<Type, FieldDataConverter> getConverters() {
		return converters;
	}

	public FieldDataConverter getConverter(Type type) {
		return converters.get(type);
	}

	@Override
	public String toString() {
		return "ConverterMapping{" +
			"converters=" + converters +
			'}';
	}
}
