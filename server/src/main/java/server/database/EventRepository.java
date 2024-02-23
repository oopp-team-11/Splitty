package server.database;


import org.springframework.data.jpa.repository.JpaRepository;
import commons.Participant;

public interface EventRepository extends JpaRepository<Participant, Long> {}

