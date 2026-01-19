package dev.czescjestemadam.database.repository;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.exceptions.DatabaseException;
import dev.czescjestemadam.database.exceptions.ModelException;
import dev.czescjestemadam.database.exceptions.ModelNotFoundException;
import dev.czescjestemadam.database.exceptions.QueryException;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.query.builder.InsertQueryBuilder;
import dev.czescjestemadam.database.query.builder.QueryBuilder;
import dev.czescjestemadam.database.query.impl.UpdateQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractRepository<T extends Model<T>> implements Repository<T> {
	protected final DatabaseConnectionManager manager;
	protected final Class<T> modelClass;

	protected AbstractRepository(DatabaseConnectionManager manager, Class<T> modelClass) {
		this.manager = manager;
		this.modelClass = modelClass;
	}

	@Nullable
	@Override
	public T find(int id) {
		return connected(connection -> {
			return find(connection, id);
		});
	}

	@NotNull
	@Override
	public T findOrFail(int id) {
		final T model = find(id);
		if (model == null) {
			throw new ModelNotFoundException(String.format(
					"Model %s with id %d not found",
					modelClass,
					id
			));
		}
		return model;
	}

	@Override
	public boolean exists(int id) {
		return connected(connection -> {
			final ResultSet resultSet = query("id")
					.whereEquals("id", id)
					.select()
					.execute(connection);

			return resultSet.first();
		});
	}

	@Override
	public void update(T model) {
		connected(connection -> {
			final Map<String, Object> dirtyValues = model.getDirtyValues();

			query()
					.whereEquals("id", model.getId())
					.update(new ArrayList<>(dirtyValues.entrySet()))
					.execute(connection);
		});
	}

	@Override
	public boolean delete(int id) {
		return connected(connection -> {
			final Integer updateCount = query()
					.whereEquals("id", id)
					.delete()
					.execute(connection);

			return updateCount > 0;
		});
	}

	@Override
	public T insert(T model) {
		final T inserted = insert(model.getValues());

		model.initValues(inserted.getValues());
		return model;
	}

	@Override
	public T insert(Map<String, Object> values) {

		return connected(connection -> {
			final UpdateQuery query = insertQuery()
					.columns(values.keySet())
					.values(new ArrayList<>(values.values()))
					.build(true);

			final Integer updateCount = query.execute(connection);

			if (updateCount > 0) {
				final ResultSet generatedKeys = query.getGeneratedKeys();

				if (generatedKeys.next()) {
					final int lastInsertRowId = generatedKeys.getInt(1);
					return find(connection, lastInsertRowId);
				}
			}

			return null;
		});
	}

	@Override
	public void insert(Collection<T> models) {
		if (models.isEmpty()) {
			throw new IllegalArgumentException("Cannot insert empty models collection");
		}

		insertValues(
				models.stream()
						.map(Model::getValues)
						.toList()
		);
	}

	@Override
	public void insertValues(Collection<Map<String, Object>> valuesList) {
		if (valuesList.isEmpty()) {
			throw new IllegalArgumentException("Cannot insert empty models collection");
		}

		connected(connection -> {
			final InsertQueryBuilder queryBuilder = insertQuery();

			boolean columnsSet = false;
			for (final Map<String, Object> values : valuesList) {
				if (!columnsSet) {
					queryBuilder.columns(values.keySet());
					columnsSet = true;
				}

				queryBuilder.values(new ArrayList<>(values.values()));
			}

			queryBuilder.build()
					.execute(connection);
		});
	}

	@Override
	public long count() {
		return connected(connection -> {
			final ResultSet resultSet = query("COUNT(*)")
					.select()
					.execute(connection);

			if (resultSet.next()) {
				return resultSet.getLong(1);
			} else {
				throw new QueryException("No results from query");
			}
		});
	}

	@Override
	public QueryBuilder query(String... columns) {
		return new QueryBuilder(getTableName(), Set.of(columns));
	}

	@Override
	public InsertQueryBuilder insertQuery() {
		return new InsertQueryBuilder(getTableName());
	}


	protected T createModelInstance() {
		final Constructor<T> constructor = getModelConstructor();

		try {
			return constructor.newInstance();
		} catch (final InstantiationException e) {
			throw new ModelException("Cannot instantiate model " + modelClass, e);
		} catch (final IllegalAccessException e) {
			throw new ModelException("Cannot access constructor for " + modelClass, e);
		} catch (final InvocationTargetException e) {
			throw new ModelException("Error constructing model " + modelClass, e);
		}
	}

	protected Map<String, Object> getResultSetValues(ResultSet resultSet) throws SQLException {
		final Map<String, Object> values = new HashMap<>();

		final ResultSetMetaData metaData = resultSet.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			final String columnName = metaData.getColumnName(i);
			final Object value = resultSet.getObject(i);

			values.put(columnName, value);
		}

		return values;
	}

	private Constructor<T> getModelConstructor() {
		try {
			return modelClass.getConstructor();
		} catch (final NoSuchMethodException e) {
			throw new ModelException("Default constructor not found", e);
		}
	}

	private String getTableName() {
		return Model.getTableName(modelClass);
	}

	private T find(Connection connection, int id) throws SQLException {
		final ResultSet resultSet = query()
				.whereEquals("id", id)
				.select()
				.execute(connection);

		if (resultSet.next()) {
			final T model = createModelInstance();
			model.initValues(getResultSetValues(resultSet));
			return model;
		} else {
			return null;
		}
	}

	private <R> R connected(ConnectedFunction<R> func) {
		try (final Connection connection = manager.getConnection()) {
			return func.apply(connection);
		} catch (final SQLException e) {
			throw new DatabaseException("Error getting connection", e);
		}
	}

	private void connected(ConnectedConsumer func) {
		try (final Connection connection = manager.getConnection()) {
			func.accept(connection);
		} catch (final SQLException e) {
			throw new DatabaseException("Error getting connection", e);
		}
	}

	@FunctionalInterface
	private interface ConnectedFunction<R> {
		R apply(Connection connection) throws SQLException;
	}

	@FunctionalInterface
	private interface ConnectedConsumer {
		void accept(Connection connection) throws SQLException;
	}
}
