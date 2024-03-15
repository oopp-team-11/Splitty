package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

/**
 * StompSessionHandler for handling a WebSocket client connection.
 * Listens to /event/{invitationCode} topic.
 */
public class EventStompSessionHandler extends StompSessionHandlerAdapter {
    private final String invitationCode;

    /**
     * Custom constructor for EventStompSessionHandler
     * @param invitationCode Specifies the event to which the client should listen to
     */
    public EventStompSessionHandler(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/event/" + invitationCode, this);
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String modelType = headers.getFirst("model");
        String methodType = headers.getFirst("method");
        switch (modelType) {
            case "Event" -> {
                Event receivedEvent = (Event) payload;
                //TODO: Call the correct method for handling Event updates
            }
            case "Participant" -> {
                Participant receivedParticipant = (Participant) payload;
                //TODO: Call the correct method for handling Participant updates
            }
            case "Expense" -> {
                Expense receivedExpense = (Expense) payload;
                //TODO: Call the correct method for handling Expense updates
            }
            case null, default -> System.out.println("Model type not specified in the message headers");
        }
    }
}
