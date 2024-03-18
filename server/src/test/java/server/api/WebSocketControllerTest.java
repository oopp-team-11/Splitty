package server.api;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.support.ErrorMessage;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WebSocketControllerTest {
    private WebSocketController webSocketController;
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
        principal = mock(Principal.class);
    }

    @Test
    void checkCreateEvent() {
        Event event = new Event("event");

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

        webSocketController.createEvent(principal, headers, event);

        assertTrue(eventRepo.existsById(event.getId()));

        var repoEvent = eventRepo.getReferenceById(event.getId());

        verify(messagingTemplate).convertAndSend(repoEvent);

        assertNotNull(repoEvent.getCreationDate());
        assertNotNull(repoEvent.getLastActivity());
    }

    @Test
    void checkCreateEventNull() {
        Event event = new Event(null);

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

        webSocketController.createEvent(principal, headers, event);

        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()),eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkCreateEventWrongClass() {
        Participant participant = new Participant();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "create");

        webSocketController.createEvent(principal, headers, participant);

        assertFalse(eventRepo.existsById(participant.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkCreateEventWrongHeaders1() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Participant");
        headers.add("method", "create");
        webSocketController.createEvent(principal, headers, event);
        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }

    @Test
    void checkCreateEventWrongHeaders2() {
        Event event = new Event();

        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", "update");
        webSocketController.createEvent(principal, headers, event);
        assertFalse(eventRepo.existsById(event.getId()));
        verify(messagingTemplate).convertAndSendToUser(
                eq(principal.getName()) ,eq("/queue/reply"), any(ErrorMessage.class));
    }
}
