package client.utils;

import client.scenes.MainCtrl;
import client.utils.frameHandlers.*;
import commons.Event;
import commons.Expense;
import commons.Involved;
import commons.Participant;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * StompSessionHandler for handling a WebSocket client connection.
 * Listens to /event/{invitationCode} topic.
 */
public class WebsocketSessionHandler extends StompSessionHandlerAdapter {
    private UUID invitationCode;
    private List<StompSession.Subscription> eventSubscriptions;
    private List<StompSession.Subscription> adminSubscriptions;
    private final EventDataHandler dataHandler;
    private final AdminDataHandler adminDataHandler;
    private final MainCtrl mainCtrl;
    private StompSession session;

    /**
     * Custom constructor for WebsocketSessionHandler
     *
     * @param dataHandler    dataHandler of the client
     * @param adminDataHandler admin data handler of the client
     * @param mainCtrl       mainCtrl of the client
     */
    public WebsocketSessionHandler(EventDataHandler dataHandler, AdminDataHandler adminDataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.adminDataHandler = adminDataHandler;
        this.mainCtrl = mainCtrl;
        this.eventSubscriptions = new ArrayList<>();
        this.adminSubscriptions = new ArrayList<>();
        dataHandler.setSessionHandler(this);
        adminDataHandler.setSessionHandler(this);
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
        //Subscribe to receive status codes
        session.subscribe("/user/queue/reply", new StatusCodeHandler(mainCtrl));
        //Subscribe to user specific endpoints
        session.subscribe("/user/queue/event:read",
                new ReadEventHandler(dataHandler, mainCtrl));
        session.subscribe("/user/queue/participants:read",
                new ReadParticipantsHandler(dataHandler, mainCtrl));
        session.subscribe("/user/queue/expenses:read",
                new ReadExpensesHandler(dataHandler, mainCtrl));

        session.subscribe("/user/queue/admin/events:read",
                new AdminReadEventsHandler(adminDataHandler));
        session.subscribe("/user/queue/admin/event:dump",
                new AdminDumpEventHandler(adminDataHandler));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        throw new RuntimeException("Failure in WebSocket handling", exception);
    }

    /**
     * Whether session was established
     *
     * @return boolean
     */
    public boolean isSessionNull() {
        return session == null;
    }

    /**
     * Disconnects from the server
     */
    public void disconnectFromServer() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        session = null;
    }

    /**
     * Subscribe to event specific endpoints
     *
     * @param invitationCode invitationCode of the event to subscribe to
     */
    public void subscribeToEvent(UUID invitationCode) throws IllegalStateException {
        if (!eventSubscriptions.isEmpty())
            throw new IllegalStateException("User did not unsubscribe before subscribing to a new event.");
        this.invitationCode = invitationCode;

        //Send request for initial event read
        refreshEvent();
    }

    /**
     * Subscribes to changes to event's expenses
     */
    public void afterInitialExpenseRead() {
        //Track expenses updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:delete",
                new DeleteExpenseHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:update",
                new UpdateExpenseHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:create",
                new CreateExpenseHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/involved:update",
                new UpdateInvolvedHandler(dataHandler)));
    }

    /**
     * Subscribes to changes to event's participants and requests for initial expenses read
     */
    public void afterInitialParticipantsRead() {
        //Track participants updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:delete",
                new DeleteParticipantHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:update",
                new UpdateParticipantHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:create",
                new CreateParticipantHandler(dataHandler)));
        //Send request for initial expenses list read
        refreshExpenses();
    }

    /**
     * Subscribes to changes to event and requests for initial participants read
     */
    public void afterInitialEventRead() {
        //Track event updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/event:delete",
                new DeleteEventHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/event:update",
                new UpdateEventHandler(dataHandler)));
        //Send request for initial participant list read
        refreshParticipants();
    }

    /**
     * Subscribe to admin specific endpoints
     *
     * @param passcode admin passcode given by the user
     */
    public void subscribeToAdmin(String passcode) {
        if (!adminSubscriptions.isEmpty()) {
            System.err.println("User did not unsubscribe before subscribing to admin again.");
            return;
        }
        if(passcode == null || passcode.isEmpty())
            throw new IllegalArgumentException();

        StompHeaders headers = new StompHeaders();
        headers.setDestination("/topic/admin/event:create");
        headers.setPasscode(passcode);
        adminSubscriptions.add(session.subscribe(headers, new AdminCreateEventHandler(adminDataHandler)));

        headers = new StompHeaders();
        headers.setDestination("/topic/admin/event:delete");
        headers.setPasscode(passcode);
        adminSubscriptions.add(session.subscribe(headers, new AdminDeleteEventHandler(adminDataHandler)));

        headers = new StompHeaders();
        headers.setDestination("/topic/admin/event:update");
        headers.setPasscode(passcode);
        adminSubscriptions.add(session.subscribe(headers, new AdminUpdateEventHandler(adminDataHandler)));
    }

    /**
     * Unsubscribes from admin's specific topics
     */
    public void unsubscribeFromAdmin() throws IllegalStateException {
        for (var subscription : adminSubscriptions)
            subscription.unsubscribe();
        adminSubscriptions.clear();
    }

    /**
     * Unsubscribes from current event's specific topics and sets current invitationCode to null
     */
    public void unsubscribeFromCurrentEvent() {
        this.invitationCode = null;
        for (var subscription : eventSubscriptions)
            subscription.unsubscribe();
        eventSubscriptions.clear();
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
     * Sends a message to the server with an Involved update
     *
     * @param involved an updated Involved object
     * @param methodType supports {"update"}
     */
    public void sendInvolved(Involved involved, String methodType) {
        session.send("/app/involved:" + methodType, involved);
    }

    /**
     * Used for refreshing the Event object
     */
    public void refreshEvent() {
        session.send("/app/event:read", invitationCode);
    }

    /**
     * Used for refreshing the Participants list
     */
    public void refreshParticipants() {
        session.send("/app/participants:read", invitationCode);
    }

    /**
     * Used for refreshing the Expenses list
     */
    public void refreshExpenses() {
        session.send("/app/expenses:read", invitationCode);
    }

    /**
     * Sends a message to the server with a request to read all events
     * @param passcode admin passcode
     */
    public void sendReadEvents(String passcode) {
        session.send("/app/admin/events:read", passcode);
    }

    /**
     * Sends a message to the server with delete/import/dump request
     * @param passcode admin passcode
     * @param receivedEvent event we are concerned about
     * @param methodType type of the method (delete/import/dump)
     */
    public void sendAdminEvent(String passcode, Event receivedEvent, String methodType)
    {
        StompHeaders headers = new StompHeaders();
        headers.setDestination("/app/admin/event:"+methodType); //delete/import/dump
        headers.setPasscode(passcode);
        session.send(headers, receivedEvent);
    }

    /**
     * Getter for the mainCtrl, for use in dataHandler
     * @return current Main Ctrl
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }
}
