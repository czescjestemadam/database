package dev.czescjestemadam.database.repository;

import dev.czescjestemadam.database.DatabaseConnectionManager;
import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.model.interfaces.Timestamped;

import java.sql.Timestamp;
import java.util.function.Supplier;

public abstract class TimestampedRepository<T extends Model<T> & Timestamped> extends AbstractRepository<T> {
	protected TimestampedRepository(
		DatabaseConnectionManager manager,
		Class<T> modelClass,
		Supplier<T> modelConstructor
	) {
		super(manager, modelClass, modelConstructor);
	}

	@Override
	public T insert(T model) {
		final Timestamp now = new Timestamp(System.currentTimeMillis());
		if (model.getCreatedAt() == null) {
			model.setCreatedAt(now);
		}
		if (model.getUpdatedAt() == null) {
			model.setUpdatedAt(now);
		}

		return super.insert(model);
	}

	@Override
	public void update(T model) {
		model.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		super.update(model);
	}
}
