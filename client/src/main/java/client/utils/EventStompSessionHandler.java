package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.UUID;

/**
 * StompSessionHandler for handling a WebSocket client connection.
 * Listens to /event/{invitationCode} topic.
 */
public class EventStompSessionHandler extends StompSessionHandlerAdapter {
    private final UUID invitationCode;
    private EventDataHandler dataHandler;
    private StompSession session;

    /**
     * Custom constructor for EventStompSessionHandler
     *
     * @param invitationCode invitationCode for the event
     */
    public EventStompSessionHandler(UUID invitationCode) {
        this.invitationCode = invitationCode;
    }

    /**
     * Handles the behaviour after establishing WebSocket connection.
     *
     * @param session          StompSession
     * @param connectedHeaders Headers to send potentially with session.send()
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        session.subscribe("/topic/" + invitationCode, this);
        session.subscribe("/user/queue/reply", this);
        //TODO:Initialise eventDataHandler
        dataHandler = new EventDataHandler();
    }

    /**
     * Handles the messages from server
     *
     * @param headers Message headers
     * @param payload Updated object
     */
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String modelType = headers.getFirst("model");
        String methodType = headers.getFirst("method");
        switch (modelType) {
            case "Event" -> dataHandler.receiveEvent((Event) payload, methodType);
            case "Participant" -> dataHandler.receiveParticipant((Participant) payload, methodType);
            case "Expense" -> dataHandler.receiveExpense((Expense) payload, methodType);
            case null, default -> System.out.println("Model type invalid or not specified in the message headers");
        }
    }

    /**
     * Sends a message to the server with a Participant update
     *
     * @param participant an updated Participant object
     * @param methodType  supports {"create", "update", "delete"}
     */
    public void sendParticipant(Participant participant, String methodType) {
        session.send("/app/participant:" + methodType, participant);
    }

    /**
     * Sends a message to the server with an Event update
     *
     * @param event      an updated Event object
     * @param methodType supports {"create", "update", "delete"}
     */
    public void sendEvent(Event event, String methodType) {
        session.send("/app/event:" + methodType, event);
    }

    /**
     * Sends a message to the server with an Expense update
     *
     * @param expense    an updated Expense object
     * @param methodType supports {"create", "update", "delete"}
     */
    public void sendExpense(Expense expense, String methodType) {
        session.send("/app/expense:" + methodType, expense);
    }
}
