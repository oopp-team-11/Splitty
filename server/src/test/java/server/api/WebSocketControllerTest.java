package server.api;

import commons.Event;
import commons.Participant;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.support.ErrorMessage;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {
    private WebSocketController webSocketController;
    private EventController eventController;
    private SimpMessagingTemplate messagingTemplate;
    private TestEventRepository eventRepo;
    private TestParticipantRepository participantRepo;
    private Principal principal;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventRepo = new TestEventRepository();
        participantRepo = new TestParticipantRepository();
        webSocketController = new WebSocketController(messagingTemplate, eventRepo, participantRepo);
        eventController = new EventController(eventRepo);
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

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

//        webSocketController.createEvent(principal, headers, event);
        var response = eventController.createEventWithTitle("event");

        Event event = eventController.getEventByInvitationCode(response.getBody().getId()).getBody();

        event.setTitle("foo");

        headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "update");

        webSocketController.updateEvent(principal, headers, event);

        assertTrue(eventRepo.existsById(event.getId()));

        var repoEvent = eventRepo.getReferenceById(event.getId());

        verify(messagingTemplate, times(2)).convertAndSend(any(Event.class));

        assertEquals("foo", repoEvent.getTitle());
    }

    @Test
    void checkUpdateEventNull() {

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

//        webSocketController.createEvent(principal, headers, event);
        var response = eventController.createEventWithTitle("event");

        Event event = eventController.getEventByInvitationCode(response.getBody().getId()).getBody();

        event.setTitle(null);

        headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "update");

        webSocketController.updateEvent(principal, headers, event);

        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()),eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkUpdateEventWrongClass() {
        Participant participant = new Participant();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "update");

        webSocketController.updateEvent(principal, headers, participant);

        assertFalse(eventRepo.existsById(participant.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkUpdateEventWrongHeaders1() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Participant");
        headers.add("method", "update");
        webSocketController.updateEvent(principal, headers, event);
        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkUpdateEventWrongHeaders2() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");
        webSocketController.updateEvent(principal, headers, event);
        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkUpdateEventNotFound() {
        Event event = new Event("event");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "update");

        webSocketController.updateEvent(principal, headers, event);

        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()),eq("/queue/reply"), any(ErrorMessage.class));
    }

    //TODO: Incorporate admin password into tests for deleteEvent()
    @Test
    void checkDeleteEvent() {
        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

//        webSocketController.createEvent(principal, headers, event);
        var response = eventController.createEventWithTitle("event");

        Event event = eventController.getEventByInvitationCode(response.getBody().getId()).getBody();

        assertTrue(eventRepo.existsById(event.getId()));

        headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "delete");

        webSocketController.deleteEvent(principal, headers, event);

        assertFalse(eventRepo.existsById(event.getId()));

        verify(messagingTemplate, times(2)).convertAndSend(any(Event.class));
    }

    @Test
    void checkDeleteEventWrongClass() {
        Participant participant = new Participant();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "delete");

        webSocketController.deleteEvent(principal, headers, participant);

        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkDeleteEventWrongHeaders1() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Participant");
        headers.add("method", "delete");
        webSocketController.deleteEvent(principal, headers, event);
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkDeleteEventWrongHeaders2() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");
        webSocketController.deleteEvent(principal, headers, event);
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkDeleteEventNotFound() {
        Event event = new Event("foo");

        try {
            setId(event, UUID.randomUUID());
        } catch (IllegalAccessException ignored) {}

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "delete");

        webSocketController.deleteEvent(principal, headers, event);

        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()),eq("/queue/reply"), any(ErrorMessage.class));
    }

    //TODO: add a test for unauthorized access
}
