package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Add expense Controller for adding expenses to events
 */
public class EditExpenseCtrl implements Translatable {
    @FXML
    public TextField expenseTitle;
    @FXML
    public TextField expenseAmount;
    @FXML
    public ChoiceBox expensePaidBy;
    @FXML
    private Label addExpenseLabel;
    @FXML
    private Button createBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label whoPaidLabel;
    @FXML
    private Label whatForLabel;
    @FXML
    private Label howMuchLabel;

    private MainCtrl mainCtrl;
    private Expense expense;
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
        this.expense = expense;

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
        if(expensePaidBy.getValue() == null){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Paid By field is empty, please choose a participant.");
            alert.showAndWait();
        } else if (expenseTitle.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense title field is empty, please add a title.");
            alert.showAndWait();
        } else if (expenseAmount.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense amount field is empty, please fill in an amount.");
            alert.showAndWait();
        } else {
            try{
                var ignored = Double.parseDouble(expenseAmount.getText());
            }catch (NumberFormatException e){
                var alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Expense amount field is not a number, please fill in a number. " +
                        "\n(don't use commas, use periods instead)");
                alert.showAndWait();
                return;
            }

            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Are you sure you want to edit this expense?");
            var result = alert.showAndWait();
            if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                Participant person = null;
                String[] fullName = expensePaidBy.getValue().toString().split(" ");
                for (Participant participant : mainCtrl.getDataHandler().getParticipants()) {
                    if (Objects.equals(participant.getFirstName(), fullName[0])
                            && Objects.equals(participant.getLastName(), fullName[1])) {
                        person = participant;
                    }
                }

                expense = new Expense(expense.getId(), expenseTitle.getText(),
                        Double.parseDouble(expenseAmount.getText()),
                        person.getId(), expense.getInvitationCode());
                mainCtrl.getSessionHandler().sendExpense(expense, "update");
            }

            mainCtrl.showEventOverview();
        }

    }

    /**
     * Abort adding expense
     */
    public void abort() {
        mainCtrl.showEventOverview();
    }

    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.expenseTitle, "WhatForExample");
        labels.put(this.expenseAmount, "Amount");
        labels.put(this.addExpenseLabel, "EditAnExpense");
        labels.put(this.createBtn, "Edit");
        labels.put(this.cancelBtn, "Cancel");
        labels.put(this.whoPaidLabel, "WhoPaidLabel");
        labels.put(this.whatForLabel, "WhatForLabel");
        labels.put(this.howMuchLabel, "HowMuchLabel");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
            if (key instanceof Button)
                ((Button) key).setText(translation.replaceAll("\"", ""));
        });
    }
}
