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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public Label meLabel;
    @FXML
    public Label editTitleLabel;
    @FXML
    public Label totalSumExpenses;
    @FXML
    public ChoiceBox<Participant> userChoiceBox;
    @FXML
    public Tab myExpensesTab;
    @FXML
    public Tab involvingMeTab;
    @FXML
    public TabPane tabPaneExpenses;
    @FXML
    public Tab allExpenses;
    @FXML
    public TableColumn<Expense, String> firstNameExpense;
    @FXML
    public TableColumn<Expense, String>  lastNameExpense;
    @FXML
    public TableColumn<Expense, LocalDate>  dateColumn;
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
            button.setStyle("-fx-base: #49873a");
            button.setOnAction(event1 -> editParticipant(participant.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        deleteColumn.setCellValueFactory(participant -> {
            Button button = new Button("X");
            button.setStyle("-fx-base: #7f1a1a");
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
        participantsList.setRowFactory(participant -> {
            TableRow<Participant> row = new TableRow<>();
            row.setOnMouseClicked(triggeredEvent -> {
                if(triggeredEvent.getClickCount() == 2 && !row.isEmpty()){
                    Participant rowDate = row.getItem();
                    Dialog<String> popup = new Dialog<>();
                    popup.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    popup.setHeaderText(mainCtrl.getTranslationSupplier().getTranslation("Participants")
                            .replaceAll("\"", ""));
                    popup.setContentText(
                            mainCtrl.getTranslationSupplier().getTranslation("ParticipantFirstName")
                                    .replaceAll("\"", "")+
                                    rowDate.getFirstName() + "\n" +
                            mainCtrl.getTranslationSupplier().getTranslation("ParticipantLastName")
                                    .replaceAll("\"", "") +
                            rowDate.getLastName() + "\n" +
                            mainCtrl.getTranslationSupplier().getTranslation("ParticipantIBAN")
                                    .replaceAll("\"", "")+
                            rowDate.getIban() + "\n" +
                            mainCtrl.getTranslationSupplier().getTranslation("ParticipantBIC")
                                    .replaceAll("\"", "")+
                            rowDate.getBic());
                    popup.showAndWait();
                }
            });
            return row;
        });
        participantsList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));

        titleColumn.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getTitle()));
        amountColumn.setCellValueFactory(col -> new SimpleStringProperty(String.valueOf(col.getValue().getAmount())));
        firstNameExpense.setCellValueFactory(col ->
                new SimpleStringProperty(col.getValue().getPaidBy().getFirstName()));
        lastNameExpense.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getPaidBy().getLastName()));
        dateColumn.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getDate()));


        editColumn1.setCellValueFactory(expense -> {
            Button button = new Button("✎");
            button.setStyle("-fx-base: #49873a");
            button.setOnAction(event1 -> editExpense(expense.getValue()));
            return new SimpleObjectProperty<>(button);
        });

        deleteColumn1.setCellValueFactory(expense -> {
            Button button = new Button("X");
            button.setStyle("-fx-base: #7f1a1a");
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

        expensesList.setRowFactory(expense -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(triggeredEvent -> {
                if(triggeredEvent.getClickCount() == 2 && !row.isEmpty()){
                    System.out.println("Check");
                    mainCtrl.showDetailedExpense(row.getItem());
                }
            });
            return row;
        });

        editTitle.onMouseClickedProperty().set(event1 -> {
            editTitleClicked();
        });
        editEventTextField.onKeyPressedProperty().set(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                stopEditingTitle();
            }
        });

        sendInvitesConfirmation.setText("");

        languageSwitchPlaceHolder.getChildren().clear();
        languageSwitchPlaceHolder.getChildren().add(mainCtrl.getLanguageSwitchButton());

        userChoiceBox.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));
        userChoiceBox.setConverter(new StringConverter<Participant>() {
            @Override
            public String toString(Participant participant) {
                if(participant == null) return null;
                return participant.getFirstName() + " " + participant.getLastName();
            }

            @Override
            public Participant fromString(String str) {
                String[] names = str.split(" ");

                return mainCtrl.getDataHandler().getParticipants().stream().filter(participant ->
                        names[0].equals(participant.getFirstName())
                        && names[1].equals(participant.getLastName())).toList().getFirst();
            }
        });
        userChoiceBox.setValue(null);
        userChoiceBox.setOnAction(ignored -> {
            var tab = tabPaneExpenses.getSelectionModel().getSelectedItem();
            tabPaneExpenses.getSelectionModel().selectFirst();
            tabPaneExpenses.getSelectionModel().select(tab);
        });

        var plusIconExpense = new ImageView(new Image("icons/plus.png"));
        plusIconExpense.setPreserveRatio(true);
        plusIconExpense.setSmooth(true);
        plusIconExpense.setFitWidth(16);
        var plusIconParticipant = new ImageView(new Image("icons/plus.png"));
        plusIconParticipant.setPreserveRatio(true);
        plusIconParticipant.setSmooth(true);
        plusIconParticipant.setFitWidth(16);
        var userIcon = new ImageView(new Image("icons/user.png"));
        userIcon.setPreserveRatio(true);
        userIcon.setSmooth(true);
        userIcon.setFitWidth(16);
        var addUserIcon = new ImageView(new Image("icons/add-user.png"));
        addUserIcon.setPreserveRatio(true);
        addUserIcon.setSmooth(true);
        addUserIcon.setFitWidth(16);


        addExpenseBtn.setGraphic(plusIconExpense);
        addParticipantBtn.setGraphic(plusIconParticipant);
        meLabel.setGraphic(userIcon);
        sendInvitesButton.setGraphic(addUserIcon);

        setTabs();
    }

    private void setTabs(){
        allExpenses.setOnSelectionChanged(thisEvent -> {
            if (allExpenses.isSelected()){
                allExpenses.setContent(expensesList);
                expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));
            } else {
                allExpenses.setContent(null);
            }
        });
        myExpensesTab.setOnSelectionChanged(thisEvent -> {
            if (myExpensesTab.isSelected()){
                if (userChoiceBox.getValue() != null){
                    myExpensesTab.setContent(expensesList);
                    expensesList.setItems(FXCollections.observableList(
                            mainCtrl.getDataHandler().getExpensesByParticipant(userChoiceBox.getValue())
                    ));
                }else {
                    myExpensesTab.setContent(expensesList);
                    expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));
                }
            } else {
                myExpensesTab.setContent(null);
            }
        });
        involvingMeTab.setOnSelectionChanged(thisEvent -> {
            if (involvingMeTab.isSelected()) {
                if (userChoiceBox.getValue() != null) {
                    involvingMeTab.setContent(expensesList);
                    expensesList.setItems(FXCollections.observableList(
                            mainCtrl.getDataHandler().getExpensesByInvolvedParticipant(userChoiceBox.getValue())));
                }else {
                    involvingMeTab.setContent(expensesList);
                    expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));
                }
            } else {
                involvingMeTab.setContent(null);
            }
        });
    }

    /**
     * Toggles the option of editing the title
     */
    public void editTitleClicked() {
        if (editEventTextField.isVisible()){
            stopEditingTitle();
        }else {
            editingTitle();
        }
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
        userChoiceBox.setItems(FXCollections.observableList(new ArrayList<>()));
        userChoiceBox.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getParticipants()));
        tabPaneExpenses.getSelectionModel().select(allExpenses);
    }
    /**
     * Method for updating data in scene
     */
    public void refreshExpensesData(){
        expensesList.getColumns().getFirst().setVisible(false);
        expensesList.getColumns().getFirst().setVisible(true);
        expensesList.setItems(FXCollections.observableList(mainCtrl.getDataHandler().getExpenses()));
        tabPaneExpenses.getSelectionModel().select(allExpenses);
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
        labels.put(this.meLabel, "Me");
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
        tableColumns.put(this.firstNameExpense, "FirstName");
        tableColumns.put(this.lastNameExpense, "LastName");
        tableColumns.put(this.dateColumn, "Date");
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

        allExpenses.setText(translationSupplier.getTranslation("AllExpenses")
                .replaceAll("\"", ""));
        myExpensesTab.setText(translationSupplier.getTranslation("MyExpenses")
                .replaceAll("\"", ""));
        involvingMeTab.setText(translationSupplier.getTranslation("InvolvingMe")
                .replaceAll("\"", ""));

        totalSumExpenses.setText(translationSupplier.getTranslation("Total")
                .replaceAll("\"", "") + mainCtrl.getDataHandler().sumOfAllExpenses());
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

    /**
     * Getter for send invites button
     * @return send invites button
     */
    public Button getSendInvitesButton() {
        return sendInvitesButton;
    }

    /**
     * Getter for add participant button
     * @return add participant button
     */
    public Button getAddParticipantBtn() {
        return addParticipantBtn;
    }

    /**
     * Getter for add expense button
     * @return add expense button
     */
    public Button getAddExpenseBtn() {
        return addExpenseBtn;
    }
}