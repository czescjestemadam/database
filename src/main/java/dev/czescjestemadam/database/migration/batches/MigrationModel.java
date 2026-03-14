package dev.czescjestemadam.database.migration.batches;

import dev.czescjestemadam.database.model.Model;
import dev.czescjestemadam.database.model.annotations.Table;

import java.math.BigInteger;
import java.sql.Timestamp;

@Table("migrations")
public class MigrationModel extends Model<MigrationModel> {
	public BigInteger id;
	public Integer batchId;
	public String name;
	public Timestamp createdAt;

	public MigrationModel() {
	}

	public MigrationModel(Integer batchId, String name) {
		this(null, batchId, name, new Timestamp(System.currentTimeMillis()));
	}

	public MigrationModel(BigInteger id, Integer batchId, String name, Timestamp createdAt) {
		this.id = id;
		this.batchId = batchId;
		this.name = name;
		this.createdAt = createdAt;
	}

	@Override
	public MigrationModel copy() {
		return new MigrationModel(id, batchId, name, createdAt);
	}

	@Override
	public BigInteger getId() {
		return id;
	}

	@Override
	public String toString() {
		return "MigrationModel{" +
			"id=" + id +
			", batchId=" + batchId +
			", name='" + name + '\'' +
			", createdAt=" + createdAt +
			", original=" + original +
			'}';
	}
}
