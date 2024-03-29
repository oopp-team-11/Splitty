package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

/***
 * class CreateParticipantController
 */
public class EventOverviewCtrl implements Translatable {
    @FXML
    public Label expensesLabel;
    @FXML
    public Label sendInvitesConfirmation;
    @FXML
    public TableView<Participant> participantsList;
    @FXML
    public TableColumn<Participant, String> firstNameColumn;
    @FXML
    public TableColumn<Participant, String> lastNameColumn;
    @FXML
    public TableColumn<Participant, String> editColumn;
    @FXML
    public TableColumn<Participant, String> deleteColumn;
    @FXML
    public TableView<Expense> expensesList;
    @FXML
    public TableColumn<Expense, String> titleColumn;
    @FXML
    public TableColumn<Expense, String> amountColumn;
    @FXML
    public TableColumn<Expense, String> editColumn1;
    @FXML
    public TableColumn<Expense, String> deleteColumn1;
    @FXML
    public Label editTitle;
    @FXML
    public TextField editEventTextField;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Button sendInvitesButton;
    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;
    private Event event;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public EventOverviewCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils();
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for event name
     * @param event event object
     */
    public void setEvent(Event event) {
        this.event = event;

        setEventNameLabel(event.getTitle());

        firstNameColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getLastName()));

        Callback<TableColumn<Participant, String>, TableCell<Participant, String>> cellFactoryEditButton
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Participant, String> param) {
                        return new TableCell<Participant, String>() {

                            final Button btn = new Button("✎");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event1 ->
                                            editParticipant(getTableView().getItems().get(getIndex())));
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };
        editColumn.setCellFactory(cellFactoryEditButton);

        Callback<TableColumn<Participant, String>, TableCell<Participant, String>> cellFactoryDeleteButton
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Participant, String> param) {
                        return new TableCell<Participant, String>() {

                            final Button btn = new Button("X");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event1 ->
                                            deleteParticipant(getTableView().getItems().get(getIndex())));
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        deleteColumn.setCellFactory(cellFactoryDeleteButton);
        participantsList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));

        titleColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getTitle()));
        amountColumn.setCellValueFactory(col -> new SimpleStringProperty(String.valueOf(col.getValue().getAmount())));

        Callback<TableColumn<Expense, String>, TableCell<Expense, String>> cellFactoryEditButton1
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Expense, String> param) {
                        return new TableCell<Expense, String>() {

                            final Button btn = new Button("✎");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event1 ->
                                            editExpense(getTableView().getItems().get(getIndex())));
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };
        editColumn1.setCellFactory(cellFactoryEditButton1);

        Callback<TableColumn<Expense, String>, TableCell<Expense, String>> cellFactoryDeleteButton1
                = //
                new Callback<>() {
                    @Override
                    public TableCell call(final TableColumn<Expense, String> param) {
                        return new TableCell<Expense, String>() {

                            final Button btn = new Button("X");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event1 ->
                                            deleteExpense(getTableView().getItems().get(getIndex())));
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        deleteColumn1.setCellFactory(cellFactoryDeleteButton1);
        expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));

        editTitle.onMouseClickedProperty().set(event1 -> editingTitle());
        editEventTextField.onKeyPressedProperty().set(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                stopEditingTitle();
            }
        });

        sendInvitesConfirmation.setText("");
    }

    /**
     * Method for updating data in scene
     */
    public void refreshEventData(){
        this.event = mainCtrl.getDataHandler().getEvent();
        setEventNameLabel(event.getTitle());
    }
    /**
     * Method for updating data in scene
     */
    public void refreshParticipantsData(){
        participantsList.getColumns().getFirst().setVisible(false);
        participantsList.getColumns().getFirst().setVisible(true);
        participantsList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));
    }
    /**
     * Method for updating data in scene
     */
    public void refreshExpensesData(){
        expensesList.getColumns().getFirst().setVisible(false);
        expensesList.getColumns().getFirst().setVisible(true);
        expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));
    }

    private void editingTitle(){
        editEventTextField.setText(mainCtrl.getDataHandler().getEvent().getTitle());
        editEventTextField.setVisible(true);
        editEventTextField.setDisable(false);
        eventNameLabel.setVisible(false);
    }
    private void stopEditingTitle(){
        Event updatedEvent = mainCtrl.getDataHandler().getEvent();
        updatedEvent.setTitle(editEventTextField.getText());
        mainCtrl.getSessionHandler().sendEvent(updatedEvent, "update");
        eventNameLabel.setVisible(true);
        editEventTextField.setDisable(true);
        editEventTextField.setVisible(false);
    }

    /**
     * Translates the current scene using a translationSupplier
     * @param translationSupplier an instance of a translationSupplier. If null, the default english will be displayed.
     */
    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.sendInvitesButton, "SendInvites");
        labels.put(this.participantsLabel, "Participants");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
        });
    }

    /**
     * When button gets clicked, trigger send invites method
     */
    public void sendInvites() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(mainCtrl.getDataHandler().getEvent().getId().toString());
        clipboard.setContent(content);
        sendInvitesConfirmation.setText("Code is put on the clipboard." +
                " Invite Code:\n" + mainCtrl.getDataHandler().getEvent().getId());
    }

    /**
     * Method to delete a participant from the event
     * @param participant participant object
     */
    public void deleteParticipant(Participant participant) {
        mainCtrl.getSessionHandler().sendParticipant(participant, "delete");
    }

    /**
     * Method to add a participant to the event
     */
    public void addParticipant() {
        mainCtrl.showCreateParticipant(event);
    }

    /**
     * Method to edit a participant
     * @param participant participant object
     */
    public void editParticipant(Participant participant) {
        mainCtrl.showEditParticipant(participant);
    }

    /**
     * Setter for event name
     * @param eventName event name
     */
    public void setEventNameLabel(String eventName) {
        eventNameLabel.setText(eventName);
    }

    /**
     * Function to add expenses, it takes you to a different screen to make the expense
     */
    public void addExpense() {
        mainCtrl.showAddExpense();
    }
    /**
     * Function to edit expenses, it takes you to a different screen to edit the expense
     * @param expense - the expense to edit
     */
    public void editExpense(Expense expense) {
        mainCtrl.showEditExpense(expense);
    }
    /**
     * Function to delete expenses
     * @param expense - expense to delete
     */
    public void deleteExpense(Expense expense) {
        mainCtrl.getSessionHandler().sendExpense(expense, "delete");
    }

    /**
     * Method for going back to home screen
     */
    public void goToHome() {
        mainCtrl.getSessionHandler().unsubscribeFromCurrentEvent();
        mainCtrl.getDataHandler().setEvent(null);
        mainCtrl.showStartScreen();
    }
}