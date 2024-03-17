package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;

import java.util.List;
import java.util.UUID;

/**
 * class for event data handling
 */
public class EventDataHandler {

    private Event event;
    private List<Participant> participants;
    private List<Expense> expenses;

    /***
     * std constructor
     * @param event
     * @param participants
     * @param expenses
     */
    public EventDataHandler(Event event, List<Participant> participants, List<Expense> expenses) {
        this.event = event;
        this.participants = participants;
        this.expenses = expenses;
    }

    /***
     * std getter
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /***
     * std getter
     * @return List of participants
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /***
     * std getter
     * @return List of Expenses
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /***
     * helper method that handles the state of the participant
     * @param uuid
     * @return Participant
     */
    private Participant getParticipantById(UUID uuid) {
        for (var participant : participants) {
            if (participant.getId().equals(uuid)) {
                return participant;
            }
        }
        return null;
    }

    /***
     * helper method that updates the participant
     * @param toUpdate
     * @param fromUpdate
     */
    private void updateParticipant(Participant toUpdate, Participant fromUpdate) {
        toUpdate.setFirstName(fromUpdate.getFirstName());
        toUpdate.setLastName(fromUpdate.getLastName());
        toUpdate.setEmail(fromUpdate.getEmail());
        toUpdate.setIban(fromUpdate.getIban());
        toUpdate.setBic(fromUpdate.getBic());
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
                participants.add(receivedParticipant);
            }
            case "update" -> {
                updateParticipant(getParticipantById(receivedParticipant.getId()), receivedParticipant);
            }
            case "delete" -> {
                participants.remove(getParticipantById(receivedParticipant.getId()));
                // TODO: remove expenses
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }


    /**
     * Handles received updates on the Event object
     *
     * @param receivedEvent received Event object
     * @param methodType type of change, supports {"update", "delete"}
     */
    public void receiveEvent(Event receivedEvent, String methodType) {
        switch (methodType) {
            case "update" -> {
                event.setTitle(receivedEvent.getTitle());
            }
            case "delete" -> {
                //TODO: Discuss handling of event deletion.
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }

    /***
     * returns expense by id
     * @param uuid
     * @return Expense
     */
    private Expense getExpenseById(UUID uuid) {
        for (var expense : expenses) {
            if (expense.getId().equals(uuid)) {
                return expense;
            }
        }
        return null;
    }


    private void updateExpense(Expense toUpdate, Expense fromUpdate) {
        toUpdate.setTitle(fromUpdate.getTitle());
        toUpdate.setAmount(fromUpdate.getAmount());
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
                expenses.add(receivedExpense);
            }
            case "update" -> {
                updateExpense(getExpenseById(receivedExpense.getId()), receivedExpense);
                //TODO: discuss possible modifications or additions to this method
            }
            case "delete" -> {
                expenses.remove(receivedExpense);
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }
}
