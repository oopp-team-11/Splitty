package client.scenes;

import client.utils.EventStompSessionHandler;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.Objects;

/**
 * Add expense Controller for adding expenses to events
 */
public class EditExpenseCtrl {
    @FXML
    public TextField expenseTitle;
    @FXML
    public TextField expenseAmount;
    @FXML
    public ChoiceBox expensePaidBy;

    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;
    private EventStompSessionHandler sessionHandler;
    private Event event;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public EditExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for event
     * @param event
     * @param expense
     * @param sessionHandler
     */
    public void setEventAndExpense(Event event, Expense expense, EventStompSessionHandler sessionHandler) {
        this.event = event;
        var participantList = event.getParticipants();

        ObservableList<String> participants = FXCollections.observableArrayList(
                participantList.stream().map(participant ->
                        (participant.getFirstName() + " " + participant.getLastName())).toList());
        expensePaidBy.setItems(participants);
        expensePaidBy.setValue((expense.getPaidBy().getFirstName() + " " + expense.getPaidBy().getLastName()));
        expenseTitle.setText(expense.getTitle());
        expenseAmount.setText(String.valueOf(expense.getAmount()));
        this.sessionHandler = sessionHandler;
    }

    /**
     * Edit expense to event and participant
     */
    public void editExpense() {
        Participant person = null;
        String[] names = expensePaidBy.getValue().toString().split(" ");
        for (Participant participant : event.getParticipants()){
            if(Objects.equals(participant.getFirstName(), names[0])
                    && Objects.equals(participant.getLastName(), names[1])){
                person = participant;
            }
        }
        Expense newExpense = new Expense(person,
                expenseTitle.getText(),
                Double.parseDouble(expenseAmount.getText()));
        sessionHandler.sendExpense(newExpense, "update");

        mainCtrl.showEventOverview(event);
    }

    /**
     * Abort adding expense
     */
    public void abort() {
        mainCtrl.showEventOverview(event);
    }
}
