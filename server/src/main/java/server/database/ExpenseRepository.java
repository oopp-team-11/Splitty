package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Interface for the ExpenseRepository.
 * Automatically provides commonly used methods for interacting with the database.
 * Can be expanded by adding methods for handling custom queries on the database.
 */
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
}
