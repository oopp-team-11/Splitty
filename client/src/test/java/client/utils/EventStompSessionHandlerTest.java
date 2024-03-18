package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EventStompSessionHandlerTest {

    private UUID invitationCode;
    private EventStompSessionHandler handler;
    private StompHeaders headers;
    private StompSession session;

    @BeforeEach
    void setUp() {
        invitationCode = UUID.randomUUID();
        handler = new EventStompSessionHandler(invitationCode);
        headers = new StompHeaders();
        session = Mockito.mock(StompSession.class);
    }

    @Test
    void afterConnected() {
        handler.afterConnected(session, headers);
        verify(session, times(1)).subscribe(
                Mockito.eq("/topic/" + invitationCode),
                Mockito.eq(handler)
        );
    }

    @Test
    void sendEvent() {
        handler.afterConnected(session, headers);
        Event event1 = new Event("updatedTitle");

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.sendEvent(event1, "update");
        verify(session, times(1)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/event:update", capturedDestination);

        assertEquals(event1, payloadCaptor.getValue());
    }

    @Test
    void sendParticipant() {
        handler.afterConnected(session, headers);
        Participant participant = new Participant(new Event("dummyEvent"), "John", "Doe",
                null, null, null);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.sendParticipant(participant, "delete");
        verify(session, times(1)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/participant:delete", capturedDestination);

        assertEquals(participant, payloadCaptor.getValue());
    }

    @Test
    void sendExpense() {
        handler.afterConnected(session, headers);
        Participant participant = new Participant(new Event("dummyEvent"), "John", "Doe",
                                            null, null, null);
        Expense expense = new Expense(participant, "sampleExpense", 4.20);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.sendExpense(expense, "create");
        verify(session, times(1)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/expense:create", capturedDestination);

        assertEquals(expense, payloadCaptor.getValue());
    }
}