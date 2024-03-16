package server.database;


import org.springframework.data.jpa.repository.JpaRepository;
import commons.Event;

import java.util.UUID;

/**
 * Interface for the EventRepository.
 * Automatically provides commonly used methods for interacting with the database.
 * Can be expanded by adding methods for handling custom queries on the database.
 */
public interface EventRepository extends JpaRepository<Event, UUID> {
}


