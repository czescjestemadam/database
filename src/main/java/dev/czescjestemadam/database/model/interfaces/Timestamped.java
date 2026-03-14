package dev.czescjestemadam.database.model.interfaces;

import java.sql.Timestamp;

public interface Timestamped {
	Timestamp getCreatedAt();

	void setCreatedAt(Timestamp createdAt);

	Timestamp getUpdatedAt();

	void setUpdatedAt(Timestamp updatedAt);

	default String getCreatedAtColumn() {
		return "created_at";
	}

	default String getUpdatedAtColumn() {
		return "updated_at";
	}
}
