package client.utils;


import client.scenes.MainCtrl;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebsocketSessionHandlerTest {

    private UUID invitationCode;
    private WebsocketSessionHandler handler;
    private StompHeaders headers;
    private StompSession session;

    private static void setSubscriptions(WebsocketSessionHandler handler,
                                         List<StompSession.Subscription> subscriptions) throws IllegalAccessException {
        FieldUtils.writeField(handler, "eventSubscriptions", subscriptions, true);
    }

    @BeforeEach
    void setUp() {
        invitationCode = UUID.randomUUID();
        handler = new WebsocketSessionHandler(new EventDataHandler(), new MainCtrl());
        headers = new StompHeaders();
        session = Mockito.mock(StompSession.class);
    }

    @Test
    void afterConnected() {
        handler.afterConnected(session, headers);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);

        verify(session, times(4)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();

        assertEquals("/user/queue/reply", destinations.get(0));
        assertEquals("/user/queue/event:read", destinations.get(1));
        assertEquals("/user/queue/participants:read", destinations.get(2));
        assertEquals("/user/queue/expenses:read", destinations.get(3));
    }

    @Test
    void subscribeToEvent() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(session, times(12)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(3)).send(destinationCaptor.capture(), idCaptor.capture());

        List<UUID> uuids = idCaptor.getAllValues();
        List<String> destinations = destinationCaptor.getAllValues();

        assertEquals(invitationCode, uuids.get(0));
        assertEquals(invitationCode, uuids.get(1));
        assertEquals(invitationCode, uuids.get(2));

        assertEquals("/topic/" + invitationCode + "/event:delete", destinations.get(4));
        assertEquals("/topic/" + invitationCode + "/event:update", destinations.get(5));
        assertEquals("/topic/" + invitationCode + "/participant:delete", destinations.get(6));
        assertEquals("/topic/" + invitationCode + "/participant:update", destinations.get(7));
        assertEquals("/topic/" + invitationCode + "/participant:create", destinations.get(8));
        assertEquals("/topic/" + invitationCode + "/expense:delete", destinations.get(9));
        assertEquals("/topic/" + invitationCode + "/expense:update", destinations.get(10));
        assertEquals("/topic/" + invitationCode + "/expense:create", destinations.get(11));

        assertEquals("/app/event:read", destinations.get(12));
        assertEquals("/app/participants:read", destinations.get(13));
        assertEquals("/app/expenses:read", destinations.get(14));
    }

    @Test
    void testIllegalSubscribe() {
        StompSession.Subscription subscription1 = Mockito.mock(StompSession.Subscription.class);
        StompSession.Subscription subscription2 = Mockito.mock(StompSession.Subscription.class);
        List<StompSession.Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);
        try {
            setSubscriptions(handler, subscriptions);
        } catch (IllegalAccessException ignored) {}
        try {
            handler.subscribeToEvent(invitationCode);
        } catch (IllegalStateException exception) {
            assertTrue(true);
        }
    }

    @Test
    void unsubscribeFromEvent() {
        StompSession.Subscription subscription1 = Mockito.mock(StompSession.Subscription.class);
        StompSession.Subscription subscription2 = Mockito.mock(StompSession.Subscription.class);
        List<StompSession.Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);
        try {
            setSubscriptions(handler, subscriptions);
        } catch (IllegalAccessException ignored) {}
        handler.unsubscribeFromCurrentEvent();
        verify(subscription1).unsubscribe();
        verify(subscription2).unsubscribe();
    }

    @Test
    public void testExceptionWrapping() {
        StompCommand testCommand = StompCommand.ERROR; // Or any relevant command
        byte[] testPayload = "error-message".getBytes();
        IllegalArgumentException testException = new IllegalArgumentException("Test error");

        try {
            handler.handleException(session, testCommand, headers, testPayload, testException);
            fail("Expected RuntimeException");  // Fail if no exception is thrown
        } catch (RuntimeException e) {
            assertEquals("Failure in WebSocket handling", e.getMessage());
            assertEquals(testException, e.getCause());
        }
    }

    @Test
    void sendEvent() {
        handler.afterConnected(session, headers);
        Event event1 = new Event("updatedTitle");

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.sendEvent(event1, "update");
        verify(session).send(destinationCaptor.capture(), payloadCaptor.capture());

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
        verify(session).send(destinationCaptor.capture(), payloadCaptor.capture());

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
        verify(session).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/expense:create", capturedDestination);

        assertEquals(expense, payloadCaptor.getValue());
    }

    @Test
    void refreshEvent() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.refreshEvent();
        verify(session, times(4)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/event:read", capturedDestination);

        assertEquals(invitationCode, payloadCaptor.getValue());
    }

    @Test
    void refreshParticipants() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.refreshParticipants();
        verify(session, times(4)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/participants:read", capturedDestination);

        assertEquals(invitationCode, payloadCaptor.getValue());
    }

    @Test
    void refreshExpenses() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.refreshExpenses();
        verify(session, times(4)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/expenses:read", capturedDestination);

        assertEquals(invitationCode, payloadCaptor.getValue());
    }
}