package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;


import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


public class EventControllerTest {
    private TestEventRepository repo;
    private EventController sut;

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        sut = new EventController(repo);
    }

    // GET: ?query=title&invitationCodes={}

    @Test
    public void checkCodesNull() {
        var actual = sut.updateRecentlyAccessedEvents(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void checkCodesLengthZero() {
        var actual = sut.updateRecentlyAccessedEvents(new Long[0]);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkUpdatedEvents() {
        Event event1 = new Event("Trap1");
        Event event2 = new Event("Trap2");
        event2.setId(1L);
        Long id1 = event1.getId();
        Long id2 = event2.getId();
        repo.save(event1);
        Long[] recentEvents = new Long[2];
        recentEvents[0] = id1;
        recentEvents[1] = id2;
        var actual = sut.updateRecentlyAccessedEvents(recentEvents);
        assertEquals(OK, actual.getStatusCode());
        List<Long> expected = List.of(id1);
        List<Long> received = actual.getBody().stream().map(x -> x.getId()).toList();
        assertEquals(expected, received);
    }

    // POST: /events

    @Test
    public void checkNullTitle() {
        var actual = sut.createEventWithTitle(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void checkEmptyTitle() {
        var actual = sut.createEventWithTitle("");
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void checkThatEventIsSaved() {
        sut.createEventWithTitle("Trap");
        assertEquals("Trap", repo.events.getFirst().getTitle());
    }

    @Test
    public void checkStatusCodeWhenEventIsSaved() {
        var actual = sut.createEventWithTitle("Trap");
        assertEquals(OK, actual.getStatusCode());
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
    @Test
    public void checkEventThatDoesntExistGetParticipants() {
        Event toSave = new Event("Trap");
        var invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode + 1);
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsGetParticipants() {
        Event toSave = new Event("Trap");
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsGetParticipantsNotNull() {
        Event toSave = new Event("Trap");
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertNotNull(actual.getBody());
    }

    @Test
    public void checkEventThatExistsGetParticipantsParticipant() {
        Event toSave = new Event("Trap");
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertNotNull(actual.getBody().get(0));
    }

    @Test
    void checkEventGetParticipantsNullId() {
        var actual = sut.getParticipantsByInvitationCode(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}


