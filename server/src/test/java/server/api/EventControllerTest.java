package server.api;


import static commons.StatusEntity.ok;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;


import commons.Event;
import commons.EventList;
import commons.StatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.EventLastActivityService;
import server.PasswordService;

import java.util.List;
import java.util.UUID;


public class EventControllerTest {
    private TestEventRepository eventRepo;
    private SimpMessagingTemplate messagingTemplate;
    private EventController sut;

    private PasswordService passwordService;

    private EventLastActivityService eventLastActivityService;


    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventRepo = new TestEventRepository();
        passwordService = new PasswordService();
        eventLastActivityService = new EventLastActivityService(eventRepo, messagingTemplate);
        sut = new EventController(messagingTemplate, eventRepo, passwordService);
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
        Event event1 = new Event( "Trap1");
        Event event2 = new Event("Trap2");
        event1 = eventRepo.save(event1);
        event2 = eventRepo.save(event2);
        UUID[] recentEvents = new UUID[2];
        recentEvents[0] = event1.getId();
        recentEvents[1] = event2.getId();
        var actual = sut.updateRecentlyAccessedEvents("titles", recentEvents);
        assertEquals(OK, actual.getStatusCode());
        List<UUID> expected = List.of(event1.getId(), event2.getId());
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

        eventRepo.save(event);

        Event receivedEvent = new Event(event.getId(), "foo", event.getCreationDate(), event.getLastActivity());

        assertEquals(ok("event:update " + event.getId()), sut.updateEvent(receivedEvent));

        Event sentEvent = new Event(event.getId(), "foo", event.getCreationDate(), event.getLastActivity());
        verify(messagingTemplate).convertAndSend("/topic/" + sentEvent.getId() + "/event:update", sentEvent);

        assertTrue(eventRepo.existsById(receivedEvent.getId()));
        var repoEvent = eventRepo.getReferenceById(receivedEvent.getId());
        assertEquals("foo", repoEvent.getTitle());
    }

    @Test
    void checkUpdateEventNull() {
        Event event = new Event("event");

        event = eventRepo.save(event);

        event.setTitle(null);

        assertEquals(StatusEntity.badRequest(true, "Event title should not be empty"),
                sut.updateEvent(event));
    }

    @Test
    void checkUpdateEventNotProvided() {
        assertEquals(StatusEntity.badRequest(true, "Event object not found in message body"),
                sut.updateEvent(null));
    }

    @Test
    void checkUpdateEventNotFound() {
        Event event = new Event(UUID.randomUUID(), "event", null, null);

        assertEquals(StatusEntity.notFound(true, "Event not found"), sut.updateEvent(event));

        assertFalse(eventRepo.existsById(event.getId()));
    }

    @Test
    void checkDeleteEvent() {
        Event event = new Event("event");

        event = eventRepo.save(event);

        assertTrue(eventRepo.existsById(event.getId()));

        Event receivedEvent = new Event(event.getId(), event.getTitle(), event.getCreationDate(),
                event.getLastActivity());

        assertEquals(ok("event:delete " + event.getId()), sut.deleteEvent(receivedEvent, passwordService.getAdminPassword()));

        verify(messagingTemplate).convertAndSend("/topic/"+event.getId()+"/event:delete", receivedEvent);
        assertFalse(eventRepo.existsById(receivedEvent.getId()));
    }

    @Test
    void checkDeleteEventNotFound() {
        Event event = new Event(UUID.randomUUID(), "foo", null, null);

        assertEquals(StatusEntity.notFound(true, "Event not found"), sut.deleteEvent(event, passwordService.getAdminPassword()));

        assertFalse(eventRepo.existsById(event.getId()));
    }

    @Test
    void checkDeleteEventNull() {
        Event event = null;

        assertEquals(StatusEntity.badRequest(true, "Event should not be null"), sut.deleteEvent(event, passwordService.getAdminPassword()));
    }

    @Test
    void checkDeleteEventUnauthorizedAccess() {
        Event event = new Event("event");
        event = eventRepo.save(event);

        assertTrue(eventRepo.existsById(event.getId()));

        Event receivedEvent = new Event(event.getId(), event.getTitle(), event.getCreationDate(),
                event.getLastActivity());
        StatusEntity result = sut.deleteEvent(receivedEvent, "incorrectPassword");

        assertEquals(StatusEntity.badRequest(true, "Incorrect Password!"), result);
        assertTrue(eventRepo.existsById(receivedEvent.getId()));
    }

    @Test
    void checkReadEvent() {
        Event event = new Event("event");

        event = eventRepo.save(event);

        Event receivedEvent = new Event(event.getId(), event.getTitle(), event.getCreationDate(),
                event.getLastActivity());

        assertEquals(ok(receivedEvent), sut.readEvent(receivedEvent.getId()));
    }

    @Test
    void checkReadEventCodeNotProvided() {
        assertEquals(StatusEntity.badRequest(true, (Event) null), sut.readEvent(null));
    }

    @Test
    void checkReadEventNotFound() {
        UUID uuid = UUID.randomUUID();
        assertEquals(StatusEntity.notFound(true, (Event) null), sut.readEvent(uuid));

        assertFalse(eventRepo.existsById(uuid));
    }

    @Test
    void checkReadAllEvents() {
        Event event1 = new Event("event1");
        Event event2 = new Event("event2");

        boolean foundEvent1 = false;
        boolean foundEvent2 = false;

        event1 = eventRepo.save(event1);
        event2 = eventRepo.save(event2);

        StatusEntity received = sut.readAllEvents(passwordService.getAdminPassword());

        assertNotNull(received);
        EventList receivedEvents = received.getEventList();

        assertEquals(2, receivedEvents.size());

        for (Event e : receivedEvents) {
            if (e.getId().equals(event1.getId()) && e.getTitle().equals(event1.getTitle())) {
                foundEvent1 = true;
            }
            if (e.getId().equals(event2.getId()) && e.getTitle().equals(event2.getTitle())) {
                foundEvent2 = true;
            }
        }

        assertTrue(foundEvent1);
        assertTrue(foundEvent2);
    }

    @Test
    void checkReadAllEventsNonAdmin() {
        Event event1 = new Event("event1");
        Event event2 = new Event("event2");

        event1 = eventRepo.save(event1);
        event2 = eventRepo.save(event2);

        List<Event> eventList = List.of(event1, event2);

        EventList events = new EventList();
        events.addAll(eventList);

        StatusEntity expected = StatusEntity.badRequest(true, "Incorrect Password!");
        StatusEntity received = sut.readAllEvents("wrongPassword");

        assertEquals(expected, received);
    }
}


