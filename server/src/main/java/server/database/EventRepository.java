package server.database;


import org.springframework.data.jpa.repository.JpaRepository;
import commons.Event;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.invitationCode = ?1")
    List<Event> findOneByInvitationCode(String invitationCode);
}


