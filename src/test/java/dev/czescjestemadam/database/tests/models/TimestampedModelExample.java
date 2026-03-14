package dev.czescjestemadam.database.tests.models;

import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.model.interfaces.Timestamped;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.Objects;

public class TimestampedModelExample extends Model<TimestampedModelExample> implements Timestamped {
	public Integer id;
	public String name;
	@Nullable
	public Timestamp createdAt;
	@Nullable
	public Timestamp updatedAt;

	public TimestampedModelExample() {
	}

	public TimestampedModelExample(Integer id, String name, @Nullable Timestamp createdAt, @Nullable Timestamp updatedAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public TimestampedModelExample copy() {
		return new TimestampedModelExample(id, name, createdAt, updatedAt);
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TimestampedModelExample that)) return false;
		return Objects.equals(id, that.id) &&
			Objects.equals(name, that.name) &&
			Objects.equals(createdAt, that.createdAt) &&
			Objects.equals(updatedAt, that.updatedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, createdAt, updatedAt);
	}
}