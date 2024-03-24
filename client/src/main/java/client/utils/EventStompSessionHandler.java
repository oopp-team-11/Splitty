package client.utils;

import client.scenes.MainCtrl;
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
    private final MainCtrl mainCtrl;
    private StompSession session;

    /**
     * Custom constructor for EventStompSessionHandler
     *
     * @param invitationCode invitationCode for the event
     * @param dataHandler    dataHandler of the client
     * @param mainCtrl       mainCtrl of the client
     */
    public EventStompSessionHandler(UUID invitationCode, EventDataHandler dataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.invitationCode = invitationCode;
        this.mainCtrl = mainCtrl;
        dataHandler.setSessionHandler(this);
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

        //We want to track event deletion as soon as possible
        session.subscribe("/topic/" + invitationCode + "/event:delete",
                new DeleteEventHandler(dataHandler));
        //Subscribe to receive status codes
        session.subscribe("/user/queue/reply", new StatusCodeHandler(mainCtrl));

        //Initial event read
        session.subscribe("/user/queue/event:read",
                new ReadEventHandler(dataHandler, mainCtrl));
        session.send("/app/event:read", invitationCode);
        //Track event updates
        session.subscribe("/topic/" + invitationCode + "/event:update",
                new UpdateEventHandler(dataHandler));

        //Initial participants read
        session.subscribe("/user/queue/participants:read",
                new ReadParticipantsHandler(dataHandler, mainCtrl));
        session.send("/app/participants:read", invitationCode);
        //Track participants updates
        session.subscribe("/topic/" + invitationCode + "/participant:delete",
                new DeleteParticipantHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/participant:update",
                new UpdateParticipantHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/participant:create",
                new CreateParticipantHandler(dataHandler));

        //Initial expenses read
        session.subscribe("/user/queue/expenses:read",
                new ReadEventHandler(dataHandler, mainCtrl));
        session.send("/app/expenses:read", invitationCode);
        //Track expenses updates
        session.subscribe("/topic/" + invitationCode + "/expense:delete",
                new DeleteExpenseHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/expense:update",
                new UpdateExpenseHandler(dataHandler));
        session.subscribe("/topic/" + invitationCode + "/expense:create",
                new CreateExpenseHandler(dataHandler));
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

    /**
     * Used for refreshing the Event object in case of data synchronisation issues
     */
    public void refreshEvent() {
        session.send("/app/event:read", invitationCode);
    }

    /**
     * Used for refreshing the Participants list in case of data synchronisation issues
     */
    public void refreshParticipants() {
        session.send("/app/participants:read", invitationCode);
    }

    /**
     * Used for refreshing the Expenses list in case of data synchronisation issues
     */
    public void refreshExpenses() {
        session.send("/app/expenses:read", invitationCode);
    }
}
