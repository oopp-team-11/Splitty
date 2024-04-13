package client.scenes;

import client.interfaces.Translatable;
import client.scenes.modelWrappers.ParticipantDisplay;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Expense;
import commons.Involved;
import commons.InvolvedList;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add expense Controller for adding expenses to events
 */
public class EditExpenseCtrl implements Translatable {
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
    private final MainCtrl mainCtrl;
    private Expense expense;
    private CheckBox selectAllCheckBox;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public EditExpenseCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for event
     * @param expense
     */
    public void setExpense(Expense expense) {
        var participantList = mainCtrl.getDataHandler().getParticipants();
        this.expense = expense;

        ObservableList<ParticipantDisplay> participants = FXCollections.observableArrayList(
                participantList.stream().map(ParticipantDisplay::new).toList());
        expensePaidBy.setItems(participants);
        for (ParticipantDisplay participantDisplay : participants) {
            if (participantDisplay.getParticipant().equals(expense.getPaidBy())) {
                expensePaidBy.getSelectionModel().select(participantDisplay);
            }
        }
        expenseTitle.setText(expense.getTitle());
        expenseAmount.setText(String.valueOf(expense.getAmount()));
        setCustomConverter();
        expenseDatePicker.setValue(expense.getDate());
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
        AddExpenseCtrl.setCustomConverter(expenseDatePicker);
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
        for (Involved involved : expense.getInvolveds()) {
            if (involved.getParticipant().equals(participant.getParticipant()))
                checkBox.setSelected(true);
        }
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
        return AddExpenseCtrl.checkBadRequest(expensePaidBy, expenseTitle, expenseAmount, expenseDatePicker, mainCtrl);
    }

    /**
     * Edit expense to event and participant
     */
    public void editExpense() {
        if (checkBadRequest())
            return;
        List<Involved> chosenInvolved = new InvolvedList();
        Expense newExpense = new Expense(expense.getId(), expenseTitle.getText(),
                Double.parseDouble(expenseAmount.getText()), expensePaidBy.getValue().getParticipant().getId(),
                expense.getInvitationCode(), expenseDatePicker.getValue(), chosenInvolved);
        for (int index = 1; index < involvedParticipants.size(); index++) {
            ParticipantDisplay participant = involvedParticipants.get(index);
            if (participant.getCheckBox().isSelected()) {
                chosenInvolved.add(new Involved(false, newExpense, participant.getParticipant()));
            }
        }
        if (chosenInvolved.isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier().getTranslation("NoInvolvedsWarning"));
            alert.showAndWait();
            return;
        }
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(mainCtrl.getTranslationSupplier().getTranslation("ConfirmationEditExpense"));
        var result = alert.showAndWait();
        if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
            mainCtrl.getSessionHandler().sendExpense(newExpense, "update");
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
        labels.put(this.dateOfExpenseLabel, "DateOfExpenseLabel");
        labels.put(this.whoIsInvolvedLabel, "WhoIsInvolvedLabel");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation);
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation);
            if (key instanceof Button)
                ((Button) key).setText(translation);
        });
        if (selectAllCheckBox != null)
            selectAllCheckBox.setText(translationSupplier.getTranslation("Select All"));
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
