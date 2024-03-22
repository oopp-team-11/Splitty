package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;

import java.util.ArrayList;
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
     * default constructor
     */
    public EventDataHandler() {
    }

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

    /**
     * Setter for event. Used during initial read requests
     *
     * @param event the initial event object
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Setter for participants. Used during initial read requests
     *
     * @param participants Initial list of participants
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Setter for expenses. Used during initial read requests
     *
     * @param expenses Initial list of expenses.
     */
    public void setExpenses(List<Expense> expenses) {
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
     * Adds a new Participant to the list of Participants
     *
     * @param receivedParticipant the new Participant
     */
    public void getCreateParticipant(Participant receivedParticipant) {
    }

    /**
     * Deletes the requested Participant from the list of participants
     *
     * @param receivedParticipant the participant to be deleted
     */
    public void getDeleteParticipant(Participant receivedParticipant) {
    }

    /**
     * Updates an already existing Participant with the same id with new data
     *
     * @param receivedParticipant new data for the already existing participant
     */
    public void getUpdateParticipant(Participant receivedParticipant) {
    }

    /**
     * Updates the fields of the Event object
     *
     * @param receivedEvent Event object containing the updated fields
     */
    public void getUpdateEvent(Event receivedEvent) {
    }

    /**
     * Receives the request to delete all data related to current event and change the scene to start screen
     */
    public void getDeleteEvent() {
    }

    /**
     * Adds a new Expense to the list of Expenses
     *
     * @param receivedExpense the new Expense
     */
    public void getCreateExpense(Expense receivedExpense) {
    }

    /**
     * Updates an already existing Expense with new data
     *
     * @param receivedExpense an expense object containing updated fields
     */
    public void getUpdateExpense(Expense receivedExpense) {
    }

    /**
     * Deletes a requested Expense
     *
     * @param receivedExpense the Expense that should be deleted
     */
    public void getDeleteExpense(Expense receivedExpense) {
    }

    /**
     * Handles received updates on a Participant object
     *
     * @param receivedParticipant received Participant object
     * @param methodType          type of change
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
                List<Expense> toRemove = new ArrayList<>();
                for (var expense : expenses) {
                    if (receivedParticipant.getId().equals(expense.getPaidById())) {
                        toRemove.add(expense);
                    }
                }
                for (var expense : toRemove) {
                    expenses.remove(expense);
                }
            }
            case null, default -> System.out.println("Method type invalid or not specified in the message headers");
        }
    }


    /**
     * Handles received updates on the Event object
     *
     * @param receivedEvent received Event object
     * @param methodType    type of change, supports {"update", "delete"}
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
     * @param methodType      type of change, supports {"create", "update", "delete"}
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
