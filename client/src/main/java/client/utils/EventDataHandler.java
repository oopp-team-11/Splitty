package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.application.Platform;

import java.util.List;
import java.util.UUID;

/**
 * class for event data handling
 */
public class EventDataHandler {

    private Event event;
    private List<Participant> participants;
    private List<Expense> expenses;
    private WebsocketSessionHandler sessionHandler;

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
     * Getter for sessionHandler
     * @return returns a reference to sessionHandler
     */
    public WebsocketSessionHandler getSessionHandler() {
        return sessionHandler;
    }

    /**
     * Setter for sessionHandler used by sessionHandler constructor
     * @param sessionHandler sessionHandler
     */
    public void setSessionHandler(WebsocketSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    /**
     * Setter for event. Used during initial read requests
     *
     * @param event the initial event object
     */
    public void setEvent(Event event) {
        if (this.event == null) {
            this.event = event;
            Platform.runLater(() -> sessionHandler.getMainCtrl().showEventOverview(event));
        } else {
            this.event = event;
            Platform.runLater(() -> sessionHandler.getMainCtrl().refreshEventData());
        }
    }

    /**
     * Setter for participants. Used during initial read requests
     *
     * @param participants Initial list of participants
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
    }

    /**
     * Setter for expenses. Used during initial read requests
     *
     * @param expenses Initial list of expenses.
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
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
     * contains by id for participants
     * @param receivedParticipant
     * @return
     */
    private boolean containsParticipantById(Participant receivedParticipant) {
        for (var participant : participants) {
            if (participant.getId().equals(receivedParticipant.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * contains by id for expenses
     * @param receivedExpense
     * @return
     */
    private boolean containsExpenseById(Expense receivedExpense) {
        for (var expense : expenses) {
            if (expense.getId().equals(receivedExpense.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new Participant to the list of Participants
     *
     * @param receivedParticipant the new Participant
     */
    public void getCreateParticipant(Participant receivedParticipant) {
        if (containsParticipantById(receivedParticipant)) {
            // logic of refetching list of participants
            sessionHandler.refreshParticipants();
            // TODO: logic of pop-up
            return;
        }
        participants.add(receivedParticipant);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
    }

    /**
     * Deletes the requested Participant from the list of participants
     *
     * @param receivedParticipant the participant to be deleted
     */
    public void getDeleteParticipant(Participant receivedParticipant) {
        if (!containsParticipantById(receivedParticipant)) {
            //logic of refetching
            sessionHandler.refreshParticipants();
            // TODO: logic of pop-up
            return;
        }

        participants.remove(getParticipantById(receivedParticipant.getId()));
        for (Expense expense : expenses){
            if(expense.getPaidById() == receivedParticipant.getId()){
                expenses.remove(expense);
            }
        }
        sessionHandler.refreshExpenses();
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
    }

    /**
     * Updates an already existing Participant with the same id with new data
     *
     * @param receivedParticipant new data for the already existing participant
     */
    public void getUpdateParticipant(Participant receivedParticipant) {
        if (!containsParticipantById(receivedParticipant)) {
            //logic of refetching
            sessionHandler.refreshParticipants();
            // TODO: logic of pop-up
            return;
        }

        updateParticipant(getParticipantById(receivedParticipant.getId()), receivedParticipant);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
    }

    /**
     * Updates the fields of the Event object
     *
     * @param receivedEvent Event object containing the updated fields
     */
    public void getUpdateEvent(Event receivedEvent) {
        if (event == null) {
            // logic of pop-up
            sessionHandler.refreshEvent();
            // TODO: logic of pop-up
            return;
        }

        event.setTitle(receivedEvent.getTitle());
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshEventData());
    }

    /**
     * Receives the request to delete all data related to current event and change the scene to start screen
     */
    public void getDeleteEvent() {
        // Maybe some smarter logic will be needed
        event = null;
        participants = null;
        expenses = null;
        Platform.runLater(() -> sessionHandler.getMainCtrl().showStartScreen());
    }

    /**
     * Adds a new Expense to the list of Expenses
     *
     * @param receivedExpense the new Expense
     */
    public void getCreateExpense(Expense receivedExpense) {
        if (containsExpenseById(receivedExpense)) {
            // logic of refetching expenses from server
            sessionHandler.refreshExpenses();
            // TODO: logic of pop-up
            return;
        }

        expenses.add(receivedExpense);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
    }

    /**
     * Updates an already existing Expense with new data
     *
     * @param receivedExpense an expense object containing updated fields
     */
    public void getUpdateExpense(Expense receivedExpense) {
        if (!containsExpenseById(receivedExpense)) {
            // logic of refetching expenses
            sessionHandler.refreshExpenses();
            // TODO: logic of pop-up
            return;
        }

        updateExpense(getExpenseById(receivedExpense.getId()), receivedExpense);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
    }

    /**
     * Deletes a requested Expense
     *
     * @param receivedExpense the Expense that should be deleted
     */
    public void getDeleteExpense(Expense receivedExpense) {
        if (!containsExpenseById(receivedExpense)) {
            // logic of refetching expenses
            sessionHandler.refreshExpenses();
            // TODO: logic of pop-up
            return;
        }

        expenses.remove(getExpenseById(receivedExpense.getId()));
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
    }

    /**
     * Handles received updates on a Participant object
     *
     * @param receivedParticipant received Participant object
     * @param methodType          type of change
     */


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

    /***
     * updates the toUpdate expense with fromUpdate expense
     * @param toUpdate
     * @param fromUpdate
     */
    private static void updateExpense(Expense toUpdate, Expense fromUpdate) {
        toUpdate.setTitle(fromUpdate.getTitle());
        toUpdate.setAmount(fromUpdate.getAmount());
        toUpdate.setPaidById(fromUpdate.getPaidById());
    }
}
