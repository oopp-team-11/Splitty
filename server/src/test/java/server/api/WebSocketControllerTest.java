package server.api;

import commons.Event;
import commons.Participant;
import commons.StatusEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {
    private WebSocketController webSocketController;
    private SimpMessagingTemplate messagingTemplate;
    private TestEventRepository eventRepo;
    private Principal principal;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventRepo = new TestEventRepository();
        webSocketController = new WebSocketController(messagingTemplate, eventRepo);
        principal = mock(Principal.class);
    }

    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

//    @Test
//    void checkCreateEvent() {
//        Event event = new Event("event");
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("model", "Event");
//        headers.add("method", "create");
//
//        webSocketController.createEvent(principal, headers, event);
//
//        assertTrue(eventRepo.existsById(event.getId()));
//
//        var repoEvent = eventRepo.getReferenceById(event.getId());
//
//        verify(messagingTemplate).convertAndSend(repoEvent);
//
//        assertNotNull(repoEvent.getCreationDate());
//        assertNotNull(repoEvent.getLastActivity());
//    }
//
//    @Test
//    void checkCreateEventNull() {
//        Event event = new Event(null);
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("model", "Event");
//        headers.add("method", "create");
//
//        webSocketController.createEvent(principal, headers, event);
//
//        assertFalse(eventRepo.existsById(event.getId()));
//        verify(messagingTemplate).convertAndSendToUser(
//                eq(principal.getName()),eq("/queue/reply"), any(ErrorMessage.class));
//    }
//
//    @Test
//    void checkCreateEventWrongClass() {
//        Participant participant = new Participant();
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("model", "Event");
//        headers.add("method", "create");
//
//        webSocketController.createEvent(principal, headers, participant);
//
//        assertFalse(eventRepo.existsById(participant.getId()));
//        verify(messagingTemplate).convertAndSendToUser(
//                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
//    }
//
//    @Test
//    void checkCreateEventWrongHeaders1() {
//        Event event = new Event();
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("model", "Participant");
//        headers.add("method", "create");
//        webSocketController.createEvent(principal, headers, event);
//        assertFalse(eventRepo.existsById(event.getId()));
//        verify(messagingTemplate).convertAndSendToUser(
//                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
//    }
//
//    @Test
//    void checkCreateEventWrongHeaders2() {
//        Event event = new Event();
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("model", "Event");
//        headers.add("method", "update");
//        webSocketController.createEvent(principal, headers, event);
//        assertFalse(eventRepo.existsById(event.getId()));
//        verify(messagingTemplate).convertAndSendToUser(
//                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
//    }

    @Test
    void checkUpdateEvent() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        eventRepo.save(event);

        event.setTitle("foo");

        webSocketController.updateEvent(principal, event);

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

        webSocketController.updateEvent(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.badRequest("Title should not be empty")));
    }

    @Test
    void checkUpdateEventWrongClass() {
        Participant participant = new Participant();

        webSocketController.updateEvent(principal, participant);

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

        webSocketController.updateEvent(principal, event);
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

        webSocketController.deleteEvent(principal, event);

        verify(messagingTemplate).convertAndSend("/topic/"+event.getId(), event);
        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()), eq("queue/reply"),
                eq(StatusEntity.ok("event:delete "+event.getId())));
        assertFalse(eventRepo.existsById(event.getId()));
    }

    @Test
    void checkDeleteEventWrongClass() {
        Participant participant = new Participant();

        webSocketController.deleteEvent(principal, participant);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()) ,eq("/queue/reply"),
                eq(StatusEntity.badRequest("Payload should be an Event",true)));
    }

    @Test
    void checkDeleteEventNotFound() {
        Event event = new Event("foo");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        webSocketController.deleteEvent(principal, event);

        verify(messagingTemplate).convertAndSendToUser(eq(principal.getName()),eq("/queue/reply"),
                eq(StatusEntity.notFound("Event not found",true)));
        assertFalse(eventRepo.existsById(event.getId()));
    }

    //TODO: add a test for unauthorized access
}
