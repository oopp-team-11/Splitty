package client.scenes;

import client.interfaces.Translatable;
import client.scenes.modelWrappers.ParticipantDisplay;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    private ObservableList<ParticipantDisplay> involvedParticipants;
    private Border selectAllBorder;
    private CheckBox selectAllCheckBox;
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
        involvedListView.setCellFactory(this::involvedCellFactory);
        involvedParticipants = FXCollections.observableArrayList(participants);
        involvedParticipants.forEach(participantDisplay -> {
            participantDisplay.setCheckBox(participantCheckBox(participantDisplay));
        });
        involvedParticipants.addFirst(newSelectAllCheckBox());
        involvedListView.setItems(involvedParticipants);
    }

    /**
     * Sets custom string converter for datePicker to handle parsing exceptions
     */
    private void setCustomConverter() {
        setCustomConverter(expenseDatePicker);
    }

    static void setCustomConverter(DatePicker expenseDatePicker) {
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
                setBorder(null);
                setText(null);
                if (empty || participant == null) {
                    setGraphic(null);
                } else if (getIndex() == 0){
                    setGraphic(participant.getCheckBox());
                    setBorder(selectAllBorder);
                }
                else
                    setGraphic(participant.getCheckBox());
            }
        };
    }

    /**
     * Factory for standard checkBox
     *
     * @param participant participantDisplay
     * @return checkBox for participant
     */
    private CheckBox participantCheckBox(ParticipantDisplay participant) {
        CheckBox checkBox = new CheckBox(participant.toString());
        checkBox.setOnAction(event -> {
            involvedParticipants.getFirst().getCheckBox().setSelected(false);
        });
        return checkBox;
    }

    /**
     * Creates a new selectAll checkBox
     *
     * @return returns a selectAll checkBox
     */
    private ParticipantDisplay newSelectAllCheckBox() {
        selectAllBorder = new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.DOTTED,
                CornerRadii.EMPTY,
                new BorderWidths(0,0,1,0)));
        ParticipantDisplay dummyParticipant = new ParticipantDisplay(new Participant());
        selectAllCheckBox = new CheckBox("Select All");
        selectAllCheckBox.setOnAction(event -> {
            for (int index = 1; index < involvedParticipants.size(); index++) {
                involvedParticipants.get(index).getCheckBox().setSelected(selectAllCheckBox.isSelected());
            }
        });
        dummyParticipant.setCheckBox(selectAllCheckBox);
        return dummyParticipant;
    }

    /**
     * Simple checks for badRequests on the client side
     *
     * @return returns whether addExpense is a bad request
     */
    private boolean checkBadRequest() {
        return checkBadRequest(expensePaidBy, expenseTitle, expenseAmount, expenseDatePicker);
    }

    static boolean checkBadRequest(ChoiceBox<ParticipantDisplay> expensePaidBy, TextField expenseTitle,
                                   TextField expenseAmount, DatePicker expenseDatePicker) {
        if(expensePaidBy.getValue() == null){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Paid By field is empty, please choose a participant.");
            alert.showAndWait();
            return true;
        }
        if (expenseTitle.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense title field is empty, please add a title.");
            alert.showAndWait();
            return true;
        }
        if (expenseAmount.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense amount field is empty, please fill in an amount.");
            alert.showAndWait();
            return true;
        }
        if (expenseDatePicker.getValue() == null) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense date field is empty, please fill in a date.");
            alert.showAndWait();
            return true;
        }
        try{
            var ignored = Double.parseDouble(expenseAmount.getText());
        }catch (NumberFormatException e){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Expense amount field is not a number, please fill in a number. " +
                    "\n(don't use commas, use periods instead)");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    /**
     * Add expense to event and participant
     */
    public void addExpense() {
        if (checkBadRequest())
            return;
        List<Involved> chosenInvolved = new InvolvedList();
        Expense newExpense = new Expense(expensePaidBy.getValue().getParticipant(),
                expenseTitle.getText(),
                Double.parseDouble(expenseAmount.getText()), expenseDatePicker.getValue(), chosenInvolved);
        for (int index = 1; index < involvedParticipants.size(); index++) {
            ParticipantDisplay participant = involvedParticipants.get(index);
            if (participant.getCheckBox().isSelected()) {
                chosenInvolved.add(new Involved(false, newExpense, participant.getParticipant()));
            }
        }
        if (chosenInvolved.isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("You have not selected any involved participants.");
            alert.showAndWait();
            return;
        }
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Are you sure you want to add this expense?");
        var result = alert.showAndWait();
        if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
            mainCtrl.getSessionHandler().sendExpense(newExpense, "create");
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
        if (selectAllCheckBox != null)
            selectAllCheckBox.setText(translationSupplier.getTranslation("Select All")
                    .replaceAll("\"", ""));
    }

    /**
     * Getter for create button
     * @return create button
     */
    public Button getCreateBtn() {
        return createBtn;
    }

    /**
     * Getter for cancel button
     * @return cancel button
     */
    public Button getCancelBtn() {
        return cancelBtn;
    }
}
