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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    @FXML
    private Label whoIsInvolvedLabel;
    @FXML
    private Label dateOfExpenseLabel;
    @FXML
    private DatePicker expenseDatePicker;
    @FXML
    private ListView<Participant> involvedListView;
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
        setCustomConverter();
        expenseDatePicker.setValue(LocalDate.now());
    }

    /**
     * Sets custom string converter for datePicker to handle parsing exceptions
     */
    private void setCustomConverter() {
        final StringConverter<LocalDate> defaultConverter = expenseDatePicker.getConverter();
        expenseDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override public String toString(LocalDate value) {
                return defaultConverter.toString(value);
            }

            @Override public LocalDate fromString(String text) {
                try {
                    return defaultConverter.fromString(text);
                } catch (DateTimeParseException ex) {
                    return LocalDate.now();
                }
            }
        });
    }

    /**
     * Add expense to event and participant
     */
    public void addExpense() {
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
        } else if (expenseDatePicker.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense date field is empty, please fill in a date.");
            alert.showAndWait();
        } else{
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
            alert.setContentText("Are you sure you want to add this expense?");
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
