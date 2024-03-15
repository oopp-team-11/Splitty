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
    private final Event event;
    private StompSession session;

    /**
     * Custom constructor for EventStompSessionHandler
     *
     * @param event the reference to the event object
     */
    public EventStompSessionHandler(Event event) {
        this.event = event;
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
        session.subscribe(event.getId().toString(), this);
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
            case "Event" -> receiveEvent((Event) payload, methodType);
            case "Participant" -> receiveParticipant((Participant) payload, methodType);
            case "Expense" -> receiveExpense((Expense) payload, methodType);
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
        StompHeaders headers = new StompHeaders();
        headers.add("model", "Participant");
        headers.add("method", methodType);
        headers.setDestination(event.getId().toString());
        session.send(headers, participant);
    }

    /**
     * Handles received updates on a Participant object
     *
     * @param receivedParticipant received Participant object
     * @param methodType type of change
     */
    public void receiveParticipant(Participant receivedParticipant, String methodType) {
        switch (methodType) {
            case "create" -> {
                ///TODO: setEvent for participant, add participant to event
            }
            case "update" -> {
                //TODO: getParticipantById() and update
            }
            case "delete" -> {
                //TODO: getParticipantById() and remove. Also remove their expenses
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }

    /**
     * Sends a message to the server with an Event update
     *
     * @param event      an updated Event object
     * @param methodType supports {"create", "update", "delete"}
     */
    public void sendEvent(Event event, String methodType) {
        StompHeaders headers = new StompHeaders();
        headers.add("model", "Event");
        headers.add("method", methodType);
        headers.setDestination(event.getId().toString());
        session.send(headers, event);
    }

    /**
     * Handles received updates on the Event object
     *
     * @param receivedEvent received Event object
     * @param methodType type of change, supports {"update", "delete"}
     */
    public void receiveEvent(Event receivedEvent, String methodType) {
        switch (methodType) {
            case "update" -> event.setTitle(receivedEvent.getTitle());
            case "delete" -> {
                //TODO: Discuss handling of event deletion.
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }

    /**
     * Sends a message to the server with an Expense update
     *
     * @param expense    an updated Expense object
     * @param methodType supports {"create", "update", "delete"}
     */
    public void sendExpense(Expense expense, String methodType) {
        StompHeaders headers = new StompHeaders();
        headers.add("model", "Expense");
        headers.add("method", methodType);
        headers.setDestination(event.getId().toString());
        session.send(headers, expense);
    }

    /**
     * Handles received updates on an Expense object
     *
     * @param receivedExpense received Expense object
     * @param methodType type of change, supports {"create", "update", "delete"}
     */
    public void receiveExpense(Expense receivedExpense, String methodType) {
        switch (methodType) {
            case "create" -> {
                ///TODO: getParticipantById(), setPaidBy, add expense to participant
            }
            case "update" -> {
                //TODO: getParticipantById(), getExpenseById(), update expense
            }
            case "delete" -> {
                //TODO: getParticipantById(), removeExpenseById()
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }
}
