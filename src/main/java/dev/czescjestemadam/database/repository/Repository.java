package dev.czescjestemadam.database.repository;

import dev.czescjestemadam.database.exceptions.model.ModelNotFoundException;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.query.builder.InsertQueryBuilder;
import dev.czescjestemadam.database.query.builder.QueryBuilder;
import dev.czescjestemadam.database.query.impl.SelectQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface Repository<T extends Model<T>> {
	@Nullable
	T find(BigInteger id);

	@Nullable
	default T find(int id) {
		return find(BigInteger.valueOf(id));
	}

	/**
	 * @throws ModelNotFoundException when no model with given id found
	 */
	@NotNull
	T findOrFail(BigInteger id);

	/**
	 * @throws ModelNotFoundException when no model with given id found
	 */
	@NotNull
	default T findOrFail(int id) {
		return findOrFail(BigInteger.valueOf(id));
	}

	@Nullable
	T first(QueryBuilder queryBuilder);

	/**
	 * @throws ModelNotFoundException when no model with given query found
	 */
	@NotNull
	T firstOrFail(QueryBuilder queryBuilder);


	boolean exists(BigInteger id);

	default boolean exists(int id) {
		return exists(BigInteger.valueOf(id));
	}

	void update(T model);

	boolean delete(BigInteger id);

	default boolean delete(int id) {
		return delete(BigInteger.valueOf(id));
	}

	default boolean delete(T model) {
		return model.getId() != null && delete(model.getId());
	}

	int delete(QueryBuilder queryBuilder);

	T insert(T model);

	T insert(Map<String, Object> values);

	void insert(Collection<T> models);

	void insertValues(Collection<Map<String, Object>> valuesList);

	long count();

	long count(QueryBuilder queryBuilder);

	List<T> select(SelectQuery selectQuery);

	default List<T> all() {
		return select(query().select());
	}

	default T save(T model) {
		if (model.getId() == null) {
			return insert(model);
		}

		update(model);
		return model;
	}

	default T create(Map<String, Object> values) {
		return insert(values);
	}

	default boolean exists(QueryBuilder queryBuilder) {
		return first(queryBuilder) != null;
	}

	default T firstOrCreate(Map<String, Object> attributes, Map<String, Object> values) {
		final T existing = firstByAttributes(attributes);

		if (existing != null) {
			return existing;
		}

		final Map<String, Object> all = new LinkedHashMap<>(attributes);
		all.putAll(values);
		return insert(all);
	}

	default T firstOrCreate(Map<String, Object> attributes) {
		return firstOrCreate(attributes, Map.of());
	}

	default T updateOrCreate(Map<String, Object> attributes, Map<String, Object> values) {
		final T existing = firstByAttributes(attributes);

		if (existing == null) {
			final Map<String, Object> all = new LinkedHashMap<>(attributes);
			all.putAll(values);
			return insert(all);
		}

		existing.setValues(values);
		update(existing);
		return existing;
	}

	private QueryBuilder queryByAttributes(Map<String, Object> attributes) {
		final QueryBuilder queryBuilder = query();

		attributes.forEach(queryBuilder::whereEquals);

		return queryBuilder;
	}

	@Nullable
	private T firstByAttributes(Map<String, Object> attributes) {
		return first(queryByAttributes(attributes));
	}

	QueryBuilder query(String... columns);

	InsertQueryBuilder insertQuery();
}
