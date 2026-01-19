package dev.czescjestemadam.database;

import com.zaxxer.hikari.HikariConfig;

import java.nio.file.Path;

public class HikariConfigBuilder {
	private final HikariConfig config = new HikariConfig();

	public HikariConfigBuilder sqlite(Path path) {
		return jdbcUrl("jdbc:sqlite:" + path)
				.maxPoolSize(1);
	}

	public HikariConfigBuilder mysql(String host, int port, String db) {
		return jdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, db))
				.maxPoolSize(10);
	}

	public HikariConfigBuilder jdbcUrl(String jdbcUrl) {
		config.setJdbcUrl(jdbcUrl);
		return this;
	}

	public HikariConfigBuilder maxPoolSize(int size) {
		config.setMaximumPoolSize(size);
		return this;
	}

	public HikariConfigBuilder username(String username) {
		config.setUsername(username);
		return this;
	}

	public HikariConfigBuilder password(String password) {
		config.setPassword(password);
		return this;
	}

	public HikariConfig build() {
		return config;
	}
}
