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
        //assertEquals(expected, received);
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

    @Test
    void checkUpdateEvent() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        event.setTitle("foo");

        assertEquals(StatusEntity.ok("event:update " + event.getId()), sut.updateEvent(event));

        verify(messagingTemplate).convertAndSend("/topic/" + event.getId() + "/event:update", event);

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

        assertEquals(StatusEntity.badRequest("Event title should not be empty", true),
                sut.updateEvent(event));
    }

    @Test
    void checkUpdateEventNotProvided() {
        assertEquals(StatusEntity.badRequest("Event object not found in message body", true),
                sut.updateEvent(null));
    }

    @Test
    void checkUpdateEventNotFound() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        assertEquals(StatusEntity.notFound("Event not found", true), sut.updateEvent(event));

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
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("/queue/reply"),
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

    @Test
    void checkReadEvent() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        assertEquals(StatusEntity.ok(event), sut.readEvent(event.getId()));
    }

    @Test
    void checkReadEventCodeNotProvided() {
        assertEquals(StatusEntity.badRequest(null, true), sut.readEvent(null));
    }

    @Test
    void checkReadEventNotFound() {
        UUID uuid = UUID.randomUUID();
        assertEquals(StatusEntity.notFound(null, true), sut.readEvent(uuid));

        assertFalse(eventRepo.existsById(uuid));
    }
}


