package dev.czescjestemadam.database.repository;

import dev.czescjestemadam.database.exceptions.ModelNotFoundException;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.query.builder.InsertQueryBuilder;
import dev.czescjestemadam.database.query.builder.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface Repository<T extends Model<T>> {
	@Nullable
	T find(int id);

	/**
	 * @throws ModelNotFoundException when no model with given id found
	 */
	@NotNull
	T findOrFail(int id);

	boolean exists(int id);

	void update(T model);

	boolean delete(int id);

	default boolean delete(T model) {
		return model.getId() != null && delete(model.getId());
	}

	T insert(T model);

	T insert(Map<String, Object> values);

	void insert(Collection<T> models);

	void insertValues(Collection<Map<String, Object>> valuesList);

	long count();

	QueryBuilder query(String... columns);

	InsertQueryBuilder insertQuery();
}
