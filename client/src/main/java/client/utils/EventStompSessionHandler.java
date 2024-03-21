package client.utils;

import client.utils.frameHandlers.*;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompCommand;
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
    private final EventDataHandler dataHandler;
    private StompSession session;

    /**
     * Custom constructor for EventStompSessionHandler
     *
     * @param invitationCode invitationCode for the event
     */
    public EventStompSessionHandler(UUID invitationCode, EventDataHandler dataHandler) {
        this.dataHandler = dataHandler;
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
        session.subscribe("/topic/" + invitationCode + "/expense:create",
                new CreateExpenseHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/expense:update",
                new UpdateExpenseHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/expense:delete",
                new DeleteExpenseHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/participant:create",
                new CreateParticipantHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/participant:update",
                new UpdateParticipantHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/participant:delete",
                new DeleteParticipantHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/event:update",
                new UpdateEventHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/event:delete",
                new DeleteEventHandler(dataHandler));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        throw new RuntimeException("Failure in WebSocket handling", exception);
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
