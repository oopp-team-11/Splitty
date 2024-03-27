package client.scenes;

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
public class AddExpenseCtrl {
    @FXML
    public TextField expenseTitle;
    @FXML
    public TextField expenseAmount;
    @FXML
    public ChoiceBox expensePaidBy;

    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;
    private Event event;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public AddExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for fields
     */
    public void setFields() {
        this.event = mainCtrl.getDataHandler().getEvent();
        var participantList = mainCtrl.getDataHandler().getParticipants();

        ObservableList<String> participants = FXCollections.observableArrayList(
                participantList.stream().map(participant ->
                        (participant.getFirstName() + " " + participant.getLastName())).toList());
        expensePaidBy.setItems(participants);
        expenseTitle.clear();
        expenseAmount.clear();
    }

    /**
     * Add expense to event and participant
     */
    public void addExpense() {
        Participant person = null;
        String[] names = expensePaidBy.getValue().toString().split(" ");
        for (Participant participant : mainCtrl.getDataHandler().getParticipants()){
            if(Objects.equals(participant.getFirstName(), names[0])
                    && Objects.equals(participant.getLastName(), names[1])){
                person = participant;
            }
        }
        Expense newExpense = new Expense(person,
                expenseTitle.getText(),
                Double.parseDouble(expenseAmount.getText()));
        mainCtrl.getSessionHandler().sendExpense(newExpense, "create");

        mainCtrl.showEventOverview(event);
    }

    /**
     * Abort adding expense
     */
    public void abort() {
        mainCtrl.showEventOverview(event);
    }
}
