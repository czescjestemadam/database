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

	T insert(T model);

	T insert(Map<String, Object> values);

	void insert(Collection<T> models);

	void insertValues(Collection<Map<String, Object>> valuesList);

	long count();

	List<T> select(SelectQuery selectQuery);

	QueryBuilder query(String... columns);

	InsertQueryBuilder insertQuery();
}
