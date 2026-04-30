package dev.czescjestemadam.database.transaction;

import dev.czescjestemadam.database.exceptions.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public abstract class TransactionContext {
	private static final ThreadLocal<Transaction> CURRENT_TRANSACTION = new ThreadLocal<>();

	private TransactionContext() {
	}

	public static boolean hasActiveTransaction() {
		final Transaction transaction = CURRENT_TRANSACTION.get();
		return transaction != null && transaction.isActive();
	}

	public static Optional<Transaction> getCurrentTransaction() {
		return Optional.ofNullable(CURRENT_TRANSACTION.get());
	}

	public static Connection getConnection() throws SQLException {
		final Transaction transaction = CURRENT_TRANSACTION.get();
		if (transaction == null || !transaction.isActive()) {
			throw new TransactionException("No active transaction");
		}

		return transaction.getConnection();
	}

	public static void setCurrentTransaction(Transaction transaction) {
		if (CURRENT_TRANSACTION.get() != null) {
			throw new TransactionException("Transaction already exists in current thread");
		}
		CURRENT_TRANSACTION.set(transaction);
	}

	public static void clearCurrentTransaction() {
		CURRENT_TRANSACTION.remove();
	}
}
