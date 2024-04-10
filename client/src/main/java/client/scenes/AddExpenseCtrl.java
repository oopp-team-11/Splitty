package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
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
public class AddExpenseCtrl implements Translatable {
    @FXML
    public TextField expenseTitle;
    @FXML
    public TextField expenseAmount;
    @FXML
    public ChoiceBox<String> expensePaidBy;
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
        this.fileSystemUtils = new FileSystemUtils(mainCtrl.getTranslationSupplier());
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
        if(expensePaidBy.getValue() == null){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier().getTranslation("ExpensePaidByEmpty")
                    .replaceAll("\"", ""));
            alert.showAndWait();
        } else if (expenseTitle.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier().getTranslation("ExpenseTitleEmpty")
                    .replaceAll("\"", ""));
            alert.showAndWait();
        } else if (expenseAmount.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier().getTranslation("ExpenseAmountEmpty")
                    .replaceAll("\"", ""));
            alert.showAndWait();
        } else {
            try{
                var ignored = Double.parseDouble(expenseAmount.getText());
                if (ignored <= 0){
                    throw new NumberFormatException("Amount is negative.");
                }
            }catch (NumberFormatException e){
                var alert = new Alert(Alert.AlertType.WARNING);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(mainCtrl.getTranslationSupplier()
                        .getTranslation("ExpenseAmountWrongFormatEmpty")
                        .replaceAll("\"", ""));
                alert.showAndWait();
                return;
            }

            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier()
                    .getTranslation("ConfirmationAddingExpense")
                    .replaceAll("\"", ""));
            var result = alert.showAndWait();
            if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                Participant person = null;
                String[] names = expensePaidBy.getValue().split(" ");
                for (Participant participant : mainCtrl.getDataHandler().getParticipants()){
                    if(Objects.equals(participant.getFirstName(), names[0])
                            && Objects.equals(participant.getLastName(), names[1])){
                        person = participant;
                    }
                }
                // TODO: add data when you will adjust the scene (see two nulls in the constructor below)
                Expense newExpense = new Expense(person,
                        expenseTitle.getText(),
                        Double.parseDouble(expenseAmount.getText()), null, null);
                mainCtrl.getSessionHandler().sendExpense(newExpense, "create");
            }
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
        labels.put(this.addExpenseLabel, "AddAnExpense");
        labels.put(this.createBtn, "Create");
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
        });
    }
}
