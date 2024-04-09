package client.utils;

import commons.Event;
import commons.Expense;
import commons.Involved;
import commons.Participant;
import javafx.application.Platform;

import java.time.LocalDate;
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
    private WebsocketSessionHandler sessionHandler;

    /***
     * default constructor
     */
    public EventDataHandler() {
    }

    /***
     * std constructor
     * @param event event
     * @param participants participants
     * @param expenses expenses
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
        boolean refresh = this.event != null;
        this.event = event;
        if (refresh)
            Platform.runLater(() -> sessionHandler.getMainCtrl().refreshEventData());
    }

    /**
     * Setter for participants. Used during initial read requests
     *
     * @param participants Initial list of participants
     */
    public void setParticipants(List<Participant> participants) {
        boolean refresh = this.participants != null;
        this.participants = participants;
        if (refresh)
            Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
    }

    /**
     * Setter for expenses. Used during initial read requests
     *
     * @param expenses Initial list of expenses.
     */
    public void setExpenses(List<Expense> expenses) {
        boolean refresh = this.expenses != null;
        this.expenses = expenses;
        for (Expense expense : expenses)
            assignParticipantsInExpense(expense);
        if (refresh)
            Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
        else
            Platform.runLater(() -> sessionHandler.getMainCtrl().showEventOverview());
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
     * @param uuid uuid of participant
     * @return Participant
     */
    public Participant getParticipantById(UUID uuid) {
        for (var participant : participants) {
            if (participant.getId().equals(uuid)) {
                return participant;
            }
        }
        return null;
    }

    /***
     * helper method that updates the participant
     * @param toUpdate locally stored participant
     * @param fromUpdate participant storing new data
     */
    private void updateParticipant(Participant toUpdate, Participant fromUpdate) {
        toUpdate.setFirstName(fromUpdate.getFirstName());
        toUpdate.setLastName(fromUpdate.getLastName());
        toUpdate.setIban(fromUpdate.getIban());
        toUpdate.setBic(fromUpdate.getBic());
    }

    /**
     * contains by id for participants
     * @param participantId participant id to check if exists
     * @return boolean whether it exists
     */
    private boolean containsParticipantById(UUID participantId) {
        for (var participant : participants) {
            if (participant.getId().equals(participantId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * contains by id for expenses
     * @param expenseId expense to check whether exists locally
     * @return boolean whether it exists
     */
    private boolean containsExpenseById(UUID expenseId) {
        for (var expense : expenses) {
            if (expense.getId().equals(expenseId)) {
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
        if (containsParticipantById(receivedParticipant.getId())) {
            // logic of refreshing list of participants
            sessionHandler.refreshParticipants();
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
        Participant localParticipant = getParticipantById(receivedParticipant.getId());
        if (localParticipant == null) {
            sessionHandler.refreshParticipants();
            return;
        }
        participants.remove(localParticipant);
        expenses.removeIf(expense -> expense.getPaidById().equals(receivedParticipant.getId()));
        for (var expense : expenses) {
            if (expense.getInvolveds() != null)
                expense.getInvolveds()
                        .removeIf(involved -> involved.getParticipantId().equals(receivedParticipant.getId()));
        }
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
        //TODO: Refresh the Detailed Expense UI or go back to eventOverview
    }

    /**
     * Updates an already existing Participant with the same id with new data
     *
     * @param receivedParticipant new data for the already existing participant
     */
    public void getUpdateParticipant(Participant receivedParticipant) {
        Participant localParticipant = getParticipantById(receivedParticipant.getId());
        if (localParticipant == null) {
            sessionHandler.refreshParticipants();
            return;
        }
        updateParticipant(localParticipant, receivedParticipant);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshParticipantsData());
        //Refresh the list of expense to refresh potential changes of paidBy names in the UI
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
        //TODO: Refresh the Detailed Expense UI to refresh to refresh potential changes of paidBy and involved
        // names in the UI
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
            return;
        }

        event.setTitle(receivedEvent.getTitle());
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshEventData());
    }

    /**
     * Sets all data related to current event to null
     */
    public void setAllToNull() {
        event = null;
        participants = null;
        expenses = null;
    }

    /**
     * Receives the request to delete all data related to current event and change the scene to start screen
     */
    public void getDeleteEvent() {
        sessionHandler.unsubscribeFromCurrentEvent();
        setAllToNull();
        Platform.runLater(() -> sessionHandler.getMainCtrl().showStartScreen());
    }

    /**
     * Sets the participant fields in expense paidBy and list of involved
     *
     * @param expense expense in which to set the participants
     */
    public void assignParticipantsInExpense(Expense expense) {
        expense.setPaidBy(getParticipantById(expense.getPaidById()));
        if (expense.getPaidBy() == null) {
            expenses = null; //This allows for a sequential refresh
            sessionHandler.refreshParticipants();
            return;
        }
        if (expense.getInvolveds() != null) {
            for (Involved involved : expense.getInvolveds()) {
                involved.setParticipant(getParticipantById(involved.getParticipantId()));
                if (involved.getParticipant() == null) {
                    expenses = null; //This allows for a sequential refresh
                    sessionHandler.refreshParticipants();
                    return;
                }
            }
        }
    }

    /**
     * Adds a new Expense to the list of Expenses
     *
     * @param receivedExpense the new Expense
     */
    public void getCreateExpense(Expense receivedExpense) {
        if (containsExpenseById(receivedExpense.getId())) {
            // logic of refreshing expenses from server
            sessionHandler.refreshExpenses();
            return;
        }
        assignParticipantsInExpense(receivedExpense);
        expenses.add(receivedExpense);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
    }

    /**
     * Updates an already existing Expense with new data
     *
     * @param receivedExpense an expense object containing updated fields
     */
    public void getUpdateExpense(Expense receivedExpense) {
        Expense localExpense = getExpenseById(receivedExpense.getId());
        if (localExpense == null) {
            sessionHandler.refreshExpenses();
            return;
        }
        updateExpense(localExpense, receivedExpense);
        assignParticipantsInExpense(localExpense);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
        //TODO: Refresh the Detailed Expense UI if appropriate
    }

    /**
     * Deletes a requested Expense
     *
     * @param receivedExpense the Expense that should be deleted
     */
    public void getDeleteExpense(Expense receivedExpense) {
        Expense localExpense = getExpenseById(receivedExpense.getId());
        if (localExpense == null) {
            sessionHandler.refreshExpenses();
            return;
        }
        expenses.remove(localExpense);
        Platform.runLater(() -> sessionHandler.getMainCtrl().refreshExpensesData());
        //TODO: Refresh the Detailed Expense UI or go back to eventOverview
    }

    /***
     * returns expense by id
     * @param uuid uuid of an expense
     * @return Expense
     */
    public Expense getExpenseById(UUID uuid) {
        for (var expense : expenses) {
            if (expense.getId().equals(uuid)) {
                return expense;
            }
        }
        return null;
    }

    /**
     * Filters the expense list by participant who paid for it
     * @param participant Participant to filter by
     * @return returns a new list of expenses, which the provided participant paid for
     */
    public List<Expense> getExpensesByParticipant(Participant participant) {
        List<Expense> expensesByParticipant = new ArrayList<>();
        for (var expense : expenses) {
            if (expense.getPaidById().equals(participant.getId())) {
                expensesByParticipant.add(expense);
            }
        }
        return expensesByParticipant;
    }

    /**
     * Filters the expense list by participant who is involved in it
     * @param involvedParticipant Participant to filter by
     * @return returns a new list of expenses, which the provided participant is involved in
     */
    public List<Expense> getExpensesByInvolvedParticipant(Participant involvedParticipant) {
        List<Expense> expensesByInvolvedParticipant = new ArrayList<>();
        for (var expense : expenses) {
            for (var involved : expense.getInvolveds()) {
                if (involved.getParticipantId().equals(involvedParticipant.getId())) {
                    expensesByInvolvedParticipant.add(expense);
                }
            }
        }
        return expensesByInvolvedParticipant;
    }

    /**
     * Sums all amounts of expenses
     *
     * @return returns double representing a sum of all expenses
     */
    public double sumOfAllExpenses() {
        double sumOfExpenses = 0;
        for (var expense : expenses) {
            sumOfExpenses += expense.getAmount();
        }
        return sumOfExpenses;
    }

    /***
     * updates the toUpdate expense with fromUpdate expense
     * @param toUpdate locally existing expense
     * @param fromUpdate expense containing new data
     */
    private static void updateExpense(Expense toUpdate, Expense fromUpdate) {
        toUpdate.setTitle(fromUpdate.getTitle());
        toUpdate.setAmount(fromUpdate.getAmount());
        toUpdate.setPaidById(fromUpdate.getPaidById());
        toUpdate.setDate(fromUpdate.getDate());
        toUpdate.setInvolveds(fromUpdate.getInvolveds());
    }

    /**
     * Returns an involved object from provided expense and involved id
     * @param expense Expense in which to search involved
     * @param id id of involved
     * @return involved object from expense object
     */
    public Involved getInvolvedById(Expense expense, UUID id) {
        for (var involved : expense.getInvolveds()) {
            if (involved.getId().equals(id))
                return involved;
        }
        return null;
    }

    /**
     * Handle updates of Involved object
     *
     * @param receivedInvolved received involved object with updated data
     */
    public void getUpdateInvolved(Involved receivedInvolved) {
        Expense localExpense = getExpenseById(receivedInvolved.getExpenseId());
        if (localExpense == null) {
            sessionHandler.refreshExpenses();
            return;
        }
        Involved localInvolved = getInvolvedById(localExpense, receivedInvolved.getId());
        if (localInvolved == null) {
            sessionHandler.refreshExpenses();
            return;
        }
        updateInvolved(localInvolved, receivedInvolved);
    }

    private void updateInvolved(Involved toUpdate, Involved fromUpdate) {
        toUpdate.setIsSettled(fromUpdate.getIsSettled());
    }
}
