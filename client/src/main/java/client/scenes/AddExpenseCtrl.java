package client.scenes;

import client.interfaces.Translatable;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add expense Controller for adding expenses to events
 */
public class AddExpenseCtrl implements Translatable {
    @FXML
    public TextField expenseTitle;
    @FXML
    public TextField expenseAmount;
    @FXML
    public ChoiceBox<ParticipantDisplay> expensePaidBy;
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
    private ListView<ParticipantDisplay> involvedListView;
    @FXML
    ObservableList<BooleanProperty> selectedStates;
    private ArrayList<ParticipantDisplay> selectedParticipants;
    private final MainCtrl mainCtrl;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public AddExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for fields
     */
    public void setFields() {
        var participantList = mainCtrl.getDataHandler().getParticipants();

        ObservableList<ParticipantDisplay> participants = FXCollections.observableArrayList(
                participantList.stream().map(ParticipantDisplay::new).toList());
        expensePaidBy.setItems(participants);
        expenseTitle.clear();
        expenseAmount.clear();
        setCustomConverter();
        expenseDatePicker.setValue(LocalDate.now());
        selectedParticipants = new ArrayList<>();
        selectedStates = FXCollections.observableArrayList();
        for (int it = 0; it < participants.size(); it++) {
            selectedStates.add(new SimpleBooleanProperty(false));
        }
        involvedListView.setCellFactory(this::involvedCellFactory);
        ObservableList<ParticipantDisplay> involvedParticipants = FXCollections.observableArrayList(participants);
        involvedParticipants.addFirst(new ParticipantDisplay(new Participant()));
        involvedListView.setItems(involvedParticipants);
    }

    /**
     * Sets custom string converter for datePicker to handle parsing exceptions
     */
    private void setCustomConverter() {
        final StringConverter<LocalDate> defaultConverter = expenseDatePicker.getConverter();
        expenseDatePicker.setConverter(new StringConverter<>() {
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
     * Creates cells for the involvedListView
     *
     * @param involvedListView provided listView
     * @return returns a ListCell
     */
    private ListCell<ParticipantDisplay> involvedCellFactory(ListView<ParticipantDisplay> involvedListView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(ParticipantDisplay participant, boolean empty) {
                super.updateItem(participant, empty);
                if (empty || participant == null) {
                    setText(null);
                    setGraphic(null);
                } else if (getIndex() == 0) {
                    setGraphic(selectAllCheckBox());
                } else {
                    setGraphic(participantCheckBox(participant, getIndex()));
                }
            }
        };
    }

    private CheckBox participantCheckBox(ParticipantDisplay participant, int index) {
        CheckBox checkBox = new CheckBox(participant.toString());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !selectedParticipants.contains(participant))
                selectedParticipants.add(participant);
            else if(!newValue)
                selectedParticipants.remove(participant);
        });
        checkBox.selectedProperty().bindBidirectional(selectedStates.get(index - 1));
        return checkBox;
    }

    /**
     * Creates a new selectAll checkBox
     * @return returns a selectAll checkBox
     */
    private CheckBox selectAllCheckBox() {
        //TODO: Decide what to do with this text in terms of translation
        CheckBox checkBox = new CheckBox("Select all");
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                for (BooleanProperty selected : selectedStates) {
                    if (!selected.getValue())
                        selected.setValue(true);
                }
            }
            else {
                for (BooleanProperty selected : selectedStates)
                    if (selected.getValue())
                        selected.setValue(false);
            }
        });
        return checkBox;
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
                List<Involved> chosenInvolved = new InvolvedList();
                Expense newExpense = new Expense(expensePaidBy.getValue().getParticipant(),
                        expenseTitle.getText(),
                        Double.parseDouble(expenseAmount.getText()), expenseDatePicker.getValue(), chosenInvolved);
                for (ParticipantDisplay participant : selectedParticipants) {
                    chosenInvolved.add(new Involved(false, newExpense, participant.getParticipant()));
                }

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
        labels.put(this.dateOfExpenseLabel, "DateOfExpenseLabel");
        labels.put(this.whoIsInvolvedLabel, "WhoIsInvolvedLabel");
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
