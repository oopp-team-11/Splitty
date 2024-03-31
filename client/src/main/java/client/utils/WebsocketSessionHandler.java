package client.utils;

import client.scenes.MainCtrl;
import client.utils.frameHandlers.*;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.StatusEntity;
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
     * @param mainCtrl       mainCtrl of the client
     */
    public WebsocketSessionHandler(EventDataHandler dataHandler, AdminDataHandler adminDataHandler, MainCtrl mainCtrl) {
        this.dataHandler = dataHandler;
        this.adminDataHandler = adminDataHandler;
        this.mainCtrl = mainCtrl;
        this.eventSubscriptions = new ArrayList<>();
        this.adminSubscriptions = new ArrayList<>();
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
        //Subscribe to receive status codes
        session.subscribe("/user/queue/reply", new StatusCodeHandler(mainCtrl));
        //Subscribe to user specific endpoints
        session.subscribe("/user/queue/event:read",
                new ReadEventHandler(dataHandler, mainCtrl));
        session.subscribe("/user/queue/participants:read",
                new ReadParticipantsHandler(dataHandler, mainCtrl));
        session.subscribe("/user/queue/expenses:read",
                new ReadExpensesHandler(dataHandler, mainCtrl));

        StatusEntity statusEntity = (StatusEntity)
                session.send("/app/events:read", connectedHeaders.getPasscode());
        if(statusEntity.getStatusCode() == StatusEntity.StatusCode.OK)
            session.subscribe("user/queue/events:read",
                    new AdminReadEventsHandler(adminDataHandler));


        /*
        NOTE: This subscription should be done only after initial check of password validation
        that is done in initial send to endpoint /app/events:read
        StatusEntity should contain info whether password was correct or not to show pop up for the user
        Otherwise if the password is incorrect and the following subscription is made, the WS connection to
        the server will be lost
         */
        //TODO: Should be moved to subscribeToAdmin method
        //StompHeaders headers = new StompHeaders();
        //headers.setDestination("/topic/admin/event:create");
        //TODO: replace with an actual password
        //headers.setPasscode("secretPasscode");

        //subscription is added to a list for unsubscription purposes
        //TODO: this should be replaced with a new frame handler
        //adminSubscriptions.add(session.subscribe(headers, this));
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        throw new RuntimeException("Failure in WebSocket handling", exception);
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
        //We want to track event deletion as soon as possible
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/event:delete",
                new DeleteEventHandler(dataHandler)));
        //Initial event read
        session.send("/app/event:read", invitationCode);
        //Track event updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/event:update",
                new UpdateEventHandler(dataHandler)));

        //Initial participants read
        session.send("/app/participants:read", invitationCode);
        //Track participants updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:delete",
                new DeleteParticipantHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:update",
                new UpdateParticipantHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/participant:create",
                new CreateParticipantHandler(dataHandler)));

        //Initial expenses read
        session.send("/app/expenses:read", invitationCode);
        //Track expenses updates
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:delete",
                new DeleteExpenseHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:update",
                new UpdateExpenseHandler(dataHandler)));
        eventSubscriptions.add(session.subscribe("/topic/" + invitationCode + "/expense:create",
                new CreateExpenseHandler(dataHandler)));
    }

    /**
     * Subscribe to admin specific endpoints
     *
     * @param passcode admin passcode given by the user
     */
    public void subscribeToAdmin(String passcode) throws IllegalStateException {
        if (!adminSubscriptions.isEmpty())
            throw new IllegalStateException("User did not unsubscribe before subscribing to admin again.");

        refreshEvents(passcode);

        StompHeaders headers = new StompHeaders();
        headers.setDestination("/topic/admin/event:delete");
        headers.setPasscode(passcode);
        adminSubscriptions.add(session.subscribe(headers, new AdminDeleteEventHandler(adminDataHandler)));

        headers = new StompHeaders();
        headers.setDestination("/topic/admin/event:update");
        headers.setPasscode(passcode);
        adminSubscriptions.add(session.subscribe(headers, new AdminUpdateEventHandler(adminDataHandler)));
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

    public void refreshEvents(String passcode) {
        session.send("/app/admin/events:read", passcode);
    }

    //TODO: Add sendDeleteEvent()

    /**
     * Getter for the mainCtrl, for use in dataHandler
     * @return current Main Ctrl
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }
}
