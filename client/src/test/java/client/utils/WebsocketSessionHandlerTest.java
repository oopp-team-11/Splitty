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
import static org.mockito.Mockito.*;

class WebsocketSessionHandlerTest {

    private UUID invitationCode;
    private WebsocketSessionHandler handler;
    private StompHeaders headers;
    private StompSession session;
    private MainCtrl mainCtrl;

    private static void setSubscriptions(WebsocketSessionHandler handler,
                                         List<StompSession.Subscription> subscriptions) throws IllegalAccessException {
        FieldUtils.writeField(handler, "eventSubscriptions", subscriptions, true);
    }
    private static void setAdminSubscriptions(WebsocketSessionHandler handler,
                                         List<StompSession.Subscription> subscriptions) throws IllegalAccessException {
        FieldUtils.writeField(handler, "adminSubscriptions", subscriptions, true);
    }

    @BeforeEach
    void setUp() {
        invitationCode = UUID.randomUUID();
        mainCtrl = new MainCtrl();
        handler = new WebsocketSessionHandler(new EventDataHandler(), new AdminDataHandler(), mainCtrl);
        headers = new StompHeaders();
        session = Mockito.mock(StompSession.class);
    }

    @Test
    void getMainCtrl() {
        assertEquals(mainCtrl, handler.getMainCtrl());
    }

    @Test
    void afterConnected() {
        handler.afterConnected(session, headers);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);

        verify(session, times(6)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();

        assertEquals("/user/queue/reply", destinations.get(0));
        assertEquals("/user/queue/event:read", destinations.get(1));
        assertEquals("/user/queue/participants:read", destinations.get(2));
        assertEquals("/user/queue/expenses:read", destinations.get(3));
        assertEquals("/user/queue/admin/events:read", destinations.get(4));
        assertEquals("/user/queue/admin/event:dump", destinations.get(5));
    }

    @Test
    void afterInitialEventRead() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);
        handler.afterInitialEventRead();

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(session, times(8)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(2)).send(destinationCaptor.capture(), idCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        assertEquals("/topic/" + invitationCode + "/event:delete", destinations.get(6));
        assertEquals("/topic/" + invitationCode + "/event:update", destinations.get(7));

        assertEquals(invitationCode, idCaptor.getValue());
        assertEquals("/app/participants:read", destinations.get(9));
    }

    @Test
    void afterInitialParticipantsRead() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);
        handler.afterInitialParticipantsRead();

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(session, times(9)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(2)).send(destinationCaptor.capture(), idCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        assertEquals("/topic/" + invitationCode + "/participant:delete", destinations.get(6));
        assertEquals("/topic/" + invitationCode + "/participant:update", destinations.get(7));
        assertEquals("/topic/" + invitationCode + "/participant:create", destinations.get(8));

        assertEquals(invitationCode, idCaptor.getValue());
        assertEquals("/app/expenses:read", destinations.get(10));
    }

    @Test
    void afterInitialExpenseRead() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);
        handler.afterInitialExpenseRead();

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(session, times(9)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(1)).send(destinationCaptor.capture(), idCaptor.capture());

        List<String> destinations = destinationCaptor.getAllValues();
        assertEquals("/topic/" + invitationCode + "/expense:delete", destinations.get(6));
        assertEquals("/topic/" + invitationCode + "/expense:update", destinations.get(7));
        assertEquals("/topic/" + invitationCode + "/expense:create", destinations.get(8));
    }

    @Test
    void subscribeToEvent() {
        handler.afterConnected(session, headers);
        handler.subscribeToEvent(invitationCode);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(session, times(6)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(1)).send(destinationCaptor.capture(), idCaptor.capture());

        List<UUID> uuids = idCaptor.getAllValues();
        List<String> destinations = destinationCaptor.getAllValues();

        assertEquals(invitationCode, uuids.getFirst());
        assertEquals("/app/event:read", destinations.get(6));
    }

    @Test
    void subscribeToAdmin() {
        handler.afterConnected(session, headers);
        handler.subscribeToAdmin("42");

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<StompHeaders> headerCaptor = ArgumentCaptor.forClass(StompHeaders.class);
        ArgumentCaptor<StompFrameHandler> stompFrameHandlerCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);

        verify(session, times(6)).subscribe(destinationCaptor.capture(),
                stompFrameHandlerCaptor.capture());
        verify(session, times(3)).subscribe(headerCaptor.capture(),
                stompFrameHandlerCaptor.capture());

        List<StompHeaders> headers = headerCaptor.getAllValues();
        StompHeaders header = new StompHeaders();
        header.setPasscode("42");

        header.setDestination("/topic/admin/event:create");
        assertEquals(header, headers.get(0));
        header.setDestination("/topic/admin/event:delete");
        assertEquals(header, headers.get(1));
        header.setDestination("/topic/admin/event:update");
        assertEquals(header, headers.get(2));
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
            return;
        }
        fail();
    }

    @Test
    void testIllegalSubscribeToAdmin() {
        StompSession.Subscription subscription1 = Mockito.mock(StompSession.Subscription.class);
        StompSession.Subscription subscription2 = Mockito.mock(StompSession.Subscription.class);
        List<StompSession.Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);
        try {
            setAdminSubscriptions(handler, subscriptions);
        } catch (IllegalAccessException ignored) {}
        try {
            handler.subscribeToAdmin("");
        } catch (IllegalArgumentException exception) {
            fail();
        }
        assertTrue(true);
    }

    @Test
    void testNullPasscodeSubscribeToAdmin() {
        StompSession.Subscription subscription1 = Mockito.mock(StompSession.Subscription.class);
        StompSession.Subscription subscription2 = Mockito.mock(StompSession.Subscription.class);

        try {
            handler.subscribeToAdmin(null);
        } catch (IllegalArgumentException exception) {
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
    void unsubscribeFromAdmin() {
        StompSession.Subscription subscription1 = Mockito.mock(StompSession.Subscription.class);
        StompSession.Subscription subscription2 = Mockito.mock(StompSession.Subscription.class);
        List<StompSession.Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);
        try {
            setAdminSubscriptions(handler, subscriptions);
        } catch (IllegalAccessException ignored) {}
        handler.unsubscribeFromAdmin();
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
                 null, null);

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
                 null, null);
        // TODO: Probably adding some data in the constructor instead of the two nulls
        Expense expense = new Expense(participant, "sampleExpense", 4.20, null, null);

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
        verify(session, times(2)).send(destinationCaptor.capture(), payloadCaptor.capture());

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
        verify(session, times(2)).send(destinationCaptor.capture(), payloadCaptor.capture());

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
        verify(session, times(2)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/expenses:read", capturedDestination);

        assertEquals(invitationCode, payloadCaptor.getValue());
    }

    @Test
    void sendReadEvents() {
        handler.afterConnected(session, headers);
        handler.subscribeToAdmin("42");

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        handler.sendReadEvents("42");
        verify(session, times(1)).send(destinationCaptor.capture(), payloadCaptor.capture());

        String capturedDestination = destinationCaptor.getValue();
        assertEquals("/app/admin/events:read", capturedDestination);

        assertEquals("42", payloadCaptor.getValue());
    }

    @Test
    void sendAdminEvent() {
        handler.afterConnected(session, headers);
        handler.subscribeToAdmin("42");

        ArgumentCaptor<StompHeaders> headersCaptor = ArgumentCaptor.forClass(StompHeaders.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        Event event = new Event();

        handler.sendAdminEvent("42", event, "delete");
        verify(session, times(1)).send(headersCaptor.capture(), payloadCaptor.capture());

        StompHeaders expectedHeaders = new StompHeaders();
        expectedHeaders.setDestination("/app/admin/event:delete");
        expectedHeaders.setPasscode("42");

        StompHeaders capturedHeaders = headersCaptor.getValue();
        assertEquals(expectedHeaders, capturedHeaders);

        assertEquals(event, payloadCaptor.getValue());
    }
}