package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Participant;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventControllerTest {
    private TestEventRepository repo;
    private EventController sut;

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        sut = new EventController(repo);
    }

    // GET: /events/{id}     (get an event with specified id)

    @Test
    public void checkNullId() {
        var actual = sut.getEventByInvitationCode(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }


    @Test
    public void checkEventThatExists() {
        Event toSave = new Event("Trap");
        Long invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getEventByInvitationCode(invitationCode);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsTitle() {
        Event toSave = new Event("Trap");
        Long invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getEventByInvitationCode(invitationCode);
        assertEquals("Trap", actual.getBody().getTitle());
    }

    @Test
    public void checkEventThatDoesntExist() {
        Event toSave = new Event("Trap");
        Long invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getEventByInvitationCode(invitationCode + 1);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}


