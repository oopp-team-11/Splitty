package server.api;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;


import commons.Event;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


public class EventControllerTest {
    private TestEventRepository eventRepo;
    private SimpMessagingTemplate messagingTemplate;
    private EventController sut;
    private Principal principal;

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }


    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventRepo = new TestEventRepository();
        sut = new EventController(messagingTemplate, eventRepo);
        principal = mock(Principal.class);
    }

    // GET: ?query=title&invitationCodes={}

    @Test
    public void checkCodesNull() {
        var actual = sut.updateRecentlyAccessedEvents("titles", null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void checkCodesLengthZero() {
        var actual = sut.updateRecentlyAccessedEvents("titles", new UUID[0]);
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
        eventRepo.save(event1);
        eventRepo.save(event2);
        UUID[] recentEvents = new UUID[2];
        recentEvents[0] = id1;
        recentEvents[1] = id2;
        var actual = sut.updateRecentlyAccessedEvents("titles", recentEvents);
        assertEquals(OK, actual.getStatusCode());
        List<UUID> expected = List.of(id1, id2);
        List<UUID> received = actual.getBody().stream().map(Event::getId).toList();
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
        assertEquals("Trap", eventRepo.events.getFirst().getTitle());
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
        eventRepo.save(toSave);
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
        eventRepo.save(toSave);
        var actual = sut.getEventByInvitationCode(invitationCode);
        assertEquals("Trap", actual.getBody().getTitle());
    }

    @Test
    public void checkEventThatDoesntExist() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        eventRepo.save(toSave);
        var actual = sut.getEventByInvitationCode(UUID.randomUUID());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void checkEventThatDoesntExistGetParticipants() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        eventRepo.save(toSave);

        var actual = sut.getParticipantsByInvitationCode(UUID.randomUUID());
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsGetParticipants() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        eventRepo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void checkEventThatExistsGetParticipantsNotNull() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        eventRepo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertNotNull(actual.getBody());
    }

    @Test
    public void checkEventThatExistsGetParticipantsParticipant() {
        Event toSave = new Event("Trap");
        try {
            setId(toSave, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}
        toSave.addParticipant(new Participant());
        var invitationCode = toSave.getId();
        eventRepo.save(toSave);
        var actual = sut.getParticipantsByInvitationCode(invitationCode);
        assertNotNull(actual.getBody().get(0));
    }

    @Test
    void checkEventGetParticipantsNullId() {
        var actual = sut.getParticipantsByInvitationCode(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void checkUpdateEvent() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        event.setTitle("foo");

        sut.updateEvent(principal, event);

        verify(messagingTemplate).convertAndSend("/topic/"+event.getId(), event);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("queue/reply"),
                eq(StatusEntity.ok("event:update "+event.getId())));

        assertTrue(eventRepo.existsById(event.getId()));
        var repoEvent = eventRepo.getReferenceById(event.getId());
        assertEquals("foo", repoEvent.getTitle());
    }

    @Test
    void checkUpdateEventNull() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        event.setTitle(null);

        sut.updateEvent(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Title should not be empty")));
    }

    @Test
    void checkUpdateEventWrongClass() {
        Participant participant = new Participant();

        sut.updateEvent(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an Event", true)));

        assertFalse(eventRepo.existsById(participant.getId()));
    }

    @Test
    void checkUpdateEventNotFound() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        sut.updateEvent(principal, event);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Event not found",true)));

        assertFalse(eventRepo.existsById(event.getId()));
    }

    //TODO: Incorporate admin password into tests for deleteEvent()
    @Test
    void checkDeleteEvent() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        assertTrue(eventRepo.existsById(event.getId()));

        sut.deleteEvent(principal, event);

        verify(messagingTemplate).convertAndSend("/topic/"+event.getId(), event);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("queue/reply"),
                eq(StatusEntity.ok("event:delete "+event.getId())));
        assertFalse(eventRepo.existsById(event.getId()));
    }

    @Test
    void checkDeleteEventWrongClass() {
        Participant participant = new Participant();

        sut.deleteEvent(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an Event",true)));
    }

    @Test
    void checkDeleteEventNotFound() {
        Event event = new Event("foo");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        sut.deleteEvent(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Event not found",true)));
        assertFalse(eventRepo.existsById(event.getId()));
    }

    //TODO: add a test for unauthorized access
}


