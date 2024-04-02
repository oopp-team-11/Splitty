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
        String[] names = expensePaidBy.getValue().split(" ");
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

        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
    }

    /**
     * Abort adding expense
     */
    public void abort() {
        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
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