package server.database;

import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Interface that represents the participant repository
 */
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

}
