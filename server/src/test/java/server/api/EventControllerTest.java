package server.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


import commons.Event;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;


public class EventControllerTest {
    private TestEventRepository repo;
    private EventController sut;

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }


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
        var actual = sut.updateRecentlyAccessedEvents(new UUID[0]);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkUpdatedEvents() {
        Event event1 = new Event("Trap1");
        Event event2 = new Event("Trap2");
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        try {
            setId(event1, id1);
            setId(event2, id2);
        } catch (IllegalAccessException ignored) {}
        repo.save(event1);
        UUID[] recentEvents = new UUID[2];
        recentEvents[0] = id1;
        recentEvents[1] = id2;
        var actual = sut.updateRecentlyAccessedEvents(recentEvents);
        assertEquals(OK, actual.getStatusCode());
        List<UUID> expected = List.of(id1);
        List<UUID> received = actual.getBody().stream().map(x -> x.getId()).toList();
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
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        repo.save(toSave);
        UUID invitationCode = toSave.getId();
        var actual = sut.getEventByInvitationCode(invitationCode);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsTitle() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        UUID invitationCode = toSave.getId();
        repo.save(toSave);
        var actual = sut.getEventByInvitationCode(invitationCode);
        assertEquals("Trap", actual.getBody().getTitle());
    }

    @Test
    public void checkEventThatDoesntExist() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        repo.save(toSave);
        var actual = sut.getEventByInvitationCode(UUID.randomUUID());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
}


