package dev.czescjestemadam.database.model;

import dev.czescjestemadam.database.exceptions.ModelException;
import dev.czescjestemadam.database.model.annotations.Column;
import dev.czescjestemadam.database.model.annotations.Table;
import dev.czescjestemadam.database.utils.Str;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class Model<T extends Model<T>> {
	protected T original;


	public abstract T copy();

	public abstract Integer getId();

	public void copyToOriginal() {
		original = copy();
	}

	public String getTableName() {
		return getTableName(this.getClass());
	}

	public static <T extends Model<?>> String getTableName(Class<T> modelClass) {
		final Table table = modelClass.getAnnotation(Table.class);
		return table != null ?
				table.value() :
				Str.plural(Str.snakeCase(modelClass.getSimpleName()));
	}

	public T getOriginal() {
		return original;
	}

	public boolean isDirty() {
		return !equals(getOriginal());
	}

	public Map<String, Object> getValues() {
		final Map<String, Object> values = new LinkedHashMap<>();

		for (final Field field : getClass().getDeclaredFields()) {
			values.put(
					getColumnName(field, Column.class, Column::value),
					getFieldValue(field)
			);
		}

		return values;
	}

	public void initValues(Map<String, Object> values) {
		setValues(values);
		copyToOriginal();
	}

	public void setValues(Map<String, Object> values) {
		setValues(values, false);
	}

	public void setValues(Map<String, Object> values, boolean copyToOriginal) {
		final Map<String, Object> copiedToOriginalValues = new HashMap<>();

		for (final Field field : getClass().getDeclaredFields()) {
			final String columnName = getColumnName(field, Column.class, Column::value);

			if (!values.containsKey(columnName)) {
				continue;
			}

			if (copyToOriginal) {
				copiedToOriginalValues.put(columnName, getFieldValue(field));
			}

			try {
				field.set(this, values.get(columnName));
			} catch (final IllegalAccessException e) {
				throw new ModelException(String.format(
						"Cannot set field %s value in model %s to collect values",
						field,
						this
				));
			}
		}

		if (copyToOriginal) {
			if (original == null) {
				copyToOriginal();
			}
			getOriginal().setValues(copiedToOriginalValues, false);
		}
	}

	public Map<String, Object> getDirtyValues() {
		if (!isDirty()) {
			return Map.of();
		}

		final Map<String, Object> values = getValues();

		if (getOriginal() == null) {
			return values;
		}

		getOriginal().getValues().forEach((key, originalValue) -> {
			if (Objects.equals(originalValue, values.get(key))) {
				values.remove(key);
			}
		});

		return values;
	}


	private Object getFieldValue(Field field) {
		try {
			return field.get(this);
		} catch (final IllegalAccessException e) {
			throw new ModelException(String.format(
					"Cannot get field %s value in model %s to collect values",
					field,
					this
			));
		}
	}

	private static <T extends Annotation> String getColumnName(
			Field field,
			Class<T> annotationClass,
			Function<T, String> nameGetter
	) {
		final T annotation = field.getAnnotation(annotationClass);
		return annotation != null ?
				nameGetter.apply(annotation) :
				Str.snakeCase(field.getName());
	}
}
