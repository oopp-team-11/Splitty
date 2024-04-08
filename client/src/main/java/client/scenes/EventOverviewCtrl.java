package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.Map;

/***
 * class CreateParticipantController
 */
public class EventOverviewCtrl implements Translatable {
    @FXML
    public Pane languageSwitchPlaceHolder;
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
    public TableColumn<Participant, Button> editColumn;
    @FXML
    public TableColumn<Participant, Button> deleteColumn;
    @FXML
    public TableView<Expense> expensesList;
    @FXML
    public TableColumn<Expense, String> titleColumn;
    @FXML
    public TableColumn<Expense, String> amountColumn;
    @FXML
    public TableColumn<Expense, Button> editColumn1;
    @FXML
    public TableColumn<Expense, Button> deleteColumn1;
    @FXML
    public Button editTitle;
    @FXML
    public TextField editEventTextField;
    @FXML
    public Label changeLanguageLabel;
    @FXML
    public Label goToStartScreenLabel;
    @FXML
    public Label editTitleLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private Button addParticipantBtn;
    @FXML
    private Button addExpenseBtn;
    private Text invitationSendText = new Text("Code is put on the clipboard.");
    private Text inviteCodeText = new Text("Invitation code");
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

        editColumn.setCellValueFactory(participant -> {
            Button button = new Button("✎");
            button.setOnAction(event1 -> editParticipant(participant.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        deleteColumn.setCellValueFactory(participant -> {
            Button button = new Button("X");
            button.setOnAction(event1 -> {
                var alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Are you sure you want to delete this participant?");
                var result = alert.showAndWait();
                if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                    deleteParticipant(participant.getValue());
                }
            });
            return new SimpleObjectProperty<>(button);
        });
        participantsList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));

        titleColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getTitle()));
        amountColumn.setCellValueFactory(col -> new SimpleStringProperty(String.valueOf(col.getValue().getAmount())));

        editColumn1.setCellValueFactory(expense -> {
            Button button = new Button("✎");
            button.setOnAction(event1 -> editExpense(expense.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        deleteColumn1.setCellValueFactory(expense -> {
            Button button = new Button("X");
            button.setOnAction(event1 -> {
                var alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Are you sure you want to delete this expense?");
                var result = alert.showAndWait();
                if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                    deleteExpense(expense.getValue());
                }
            });
            return new SimpleObjectProperty<>(button);
        });
        expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));

        editTitle.onMouseClickedProperty().set(event1 -> {
            if (editEventTextField.isVisible()){
                stopEditingTitle();
            }else {
                editingTitle();
            }
        });
        editEventTextField.onKeyPressedProperty().set(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                stopEditingTitle();
            }
        });

        sendInvitesConfirmation.setText("");


        languageSwitchPlaceHolder.getChildren().clear();
        languageSwitchPlaceHolder.getChildren().add(mainCtrl.getLanguageSwitchButton());
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
        editTitle.setStyle("-fx-base: #49873a");
        editEventTextField.setText(mainCtrl.getDataHandler().getEvent().getTitle());
        editEventTextField.setVisible(true);
        editEventTextField.setDisable(false);
        eventNameLabel.setVisible(false);
    }
    private void stopEditingTitle(){
        editTitle.setStyle("");
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
        labels.put(this.expensesLabel, "Expenses");
        labels.put(this.addParticipantBtn, "AddAParticipant");
        labels.put(this.addExpenseBtn, "AddAnExpense");
        labels.put(this.changeLanguageLabel, "ChangeLanguageLabel");
        labels.put(this.goToStartScreenLabel, "GoToStartScreenLabel");
        labels.put(this.editTitleLabel, "EditTitleLabel");
        labels.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            if (key instanceof Labeled)
                ((Labeled) key).setText(translation.replaceAll("\"", ""));
            if (key instanceof TextField)
                ((TextField) key).setPromptText(translation.replaceAll("\"", ""));
        });
        Map<TableColumn, String> tableColumns = new HashMap<>();
        tableColumns.put(this.firstNameColumn, "FirstName");
        tableColumns.put(this.lastNameColumn, "LastName");
        tableColumns.put(this.editColumn, "Edit");
        tableColumns.put(this.deleteColumn, "Delete");
        tableColumns.put(this.titleColumn, "Title");
        tableColumns.put(this.amountColumn, "Amount");
        tableColumns.put(this.editColumn1, "Edit");
        tableColumns.put(this.deleteColumn1, "Delete");
        tableColumns.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            key.setText(translation.replaceAll("\"", ""));
        });

        Map<Text, String> texts = new HashMap<>();
        texts.put(this.invitationSendText, "InvitationSendText");
        texts.put(this.inviteCodeText, "InvitationCode");
        texts.forEach((key, val) -> {
            var translation = translationSupplier.getTranslation(val);
            if (translation == null) return;
            key.setText(translation.replaceAll("\"", ""));
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
        sendInvitesConfirmation.setText(invitationSendText.getText() +
                " " + inviteCodeText.getText() + ":\n" + mainCtrl.getDataHandler().getEvent().getId());
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
        mainCtrl.getDataHandler().setAllToNull();
        mainCtrl.showStartScreen();
    }
}