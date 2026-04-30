package dev.czescjestemadam.database.transaction;

import dev.czescjestemadam.database.exceptions.TransactionException;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class TransactionExecutor {
	private final DataSource dataSource;

	public TransactionExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public <R> R execute(TransactionFunction<R> function) {
		DatabaseTransaction transaction = null;
		try {
			transaction = new DatabaseTransaction(dataSource);
			TransactionContext.setCurrentTransaction(transaction);

			final R result = function.apply(transaction);

			if (!transaction.isCompleted()) {
				transaction.commit();
			}

			return result;
		} catch (final SQLException e) {
			if (transaction != null) {
				try {
					transaction.rollback();
				} catch (final SQLException rollbackEx) {
					e.addSuppressed(rollbackEx);
				}
			}
			throw new TransactionException("Transaction failed", e);
		} finally {
			if (transaction != null) {
				try {
					transaction.close();
				} catch (final SQLException e) {
					throw new TransactionException("Failed to close transaction", e);
				}
			}
			TransactionContext.clearCurrentTransaction();
		}
	}

	public void execute(TransactionConsumer consumer) {
		execute(transaction -> {
			consumer.accept(transaction);
			return null;
		});
	}

	@FunctionalInterface
	public interface TransactionFunction<R> {
		R apply(Transaction transaction) throws SQLException;
	}

	@FunctionalInterface
	public interface TransactionConsumer {
		void accept(Transaction transaction) throws SQLException;
	}
}
