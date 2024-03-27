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
     * @param expense
     */
    public void setExpense(Expense expense) {
        var participantList = mainCtrl.getDataHandler().getParticipants();

        ObservableList<String> participants = FXCollections.observableArrayList(
                participantList.stream().map(participant ->
                        (participant.getFirstName() + " " + participant.getLastName())).toList());
        expensePaidBy.setItems(participants);

        expensePaidBy.setValue(
                participantList.stream()
                        .filter(person -> person.getId().equals(expense.getPaidById())).map(participant ->
                                (participant.getFirstName() + " " + participant.getLastName()))
                        .toList().getFirst());
        expenseTitle.setText(expense.getTitle());
        expenseAmount.setText(String.valueOf(expense.getAmount()));
    }

    /**
     * Edit expense to event and participant
     */
    public void editExpense() {
        Participant person = null;
        String[] fullName = expensePaidBy.getValue().toString().split(" ");
        for (Participant participant : mainCtrl.getDataHandler().getParticipants()){
            if(Objects.equals(participant.getFirstName(), fullName[0])
                    && Objects.equals(participant.getLastName(), fullName[1])){
                person = participant;
            }
        }
        Expense newExpense = new Expense(person,
                expenseTitle.getText(),
                Double.parseDouble(expenseAmount.getText()));
        mainCtrl.getSessionHandler().sendExpense(newExpense, "update");

        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
    }

    /**
     * Abort adding expense
     */
    public void abort() {
        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
    }
}
