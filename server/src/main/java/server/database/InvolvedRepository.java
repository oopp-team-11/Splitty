package server.database;


import commons.Involved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Interface for the EventRepository.
 * Automatically provides commonly used methods for interacting with the database.
 * Can be expanded by adding methods for handling custom queries on the database.
 */
public interface InvolvedRepository extends JpaRepository<Involved, UUID> {
}


