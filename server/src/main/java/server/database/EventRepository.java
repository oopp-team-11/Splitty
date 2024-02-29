package server.database;


import org.springframework.data.jpa.repository.JpaRepository;
import commons.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}


