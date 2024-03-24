package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/***
 * class CreateParticipantController
 */
public class EventOverviewCtrl {
    @FXML
    public Label expensesLabel;
    @FXML
    public VBox expenseTitlesVbox;
    @FXML
    public VBox expenseEditVbox;
    @FXML
    public VBox expenseDeleteVbox;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private VBox namesVbox;
    @FXML
    private VBox editVbox;
    @FXML
    private VBox deleteVbox;
    private MainCtrl mainCtrl;
    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;
    private Event event;
    private TranslationSupplier translationSupplier;

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
        namesVbox.getChildren().clear();
        editVbox.getChildren().clear();
        deleteVbox.getChildren().clear();
        for (Participant participant : event.getParticipants()) {

            Label label = new Label(participant.getFirstName() + " " + participant.getLastName());
            label.setFont(new javafx.scene.text.Font("Arial", 16));
            Label deleteLabel = new Label("❌");
            deleteLabel.setAlignment(javafx.geometry.Pos.CENTER);
            deleteLabel.setFont(new javafx.scene.text.Font("Arial", 16));
            Label editLabel = new Label("✎");
            editLabel.setFont(new javafx.scene.text.Font("Arial", 16));
            editLabel.setAlignment(javafx.geometry.Pos.CENTER);

            deleteLabel.onMouseClickedProperty()
                .set(event1 -> deleteParticipant(participant));

            editLabel.onMouseClickedProperty()
                .set(event1 -> editParticipant(participant));

            namesVbox.getChildren().add(label);
            deleteVbox.getChildren().add(deleteLabel);
            editVbox.getChildren().add(editLabel);
        }

        expenseTitlesVbox.getChildren().clear();
        expenseEditVbox.getChildren().clear();
        expenseDeleteVbox.getChildren().clear();
        for (Expense expense : mainCtrl.getDataHandler().getExpenses()) {

            Label label = new Label(expense.getTitle());
            label.setFont(new javafx.scene.text.Font("Arial", 16));
            Label deleteLabel = new Label("❌");
            deleteLabel.setAlignment(javafx.geometry.Pos.CENTER);
            deleteLabel.setFont(new javafx.scene.text.Font("Arial", 16));
            Label editLabel = new Label("✎");
            editLabel.setFont(new javafx.scene.text.Font("Arial", 16));
            editLabel.setAlignment(javafx.geometry.Pos.CENTER);

            deleteLabel.onMouseClickedProperty()
                    .set(event1 -> deleteExpense(expense));

            editLabel.onMouseClickedProperty()
                    .set(event1 -> editExpense(expense));

            expenseTitlesVbox.getChildren().add(label);
            expenseEditVbox.getChildren().add(editLabel);
            expenseDeleteVbox.getChildren().add(deleteLabel);
        }
    }

    /**
     * Sets the translation supplier for this controller
     * @param tl the translation supplier that should be used
     */
    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
        this.translate();
    }

    private void translate() {
        if (this.translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.sendInvitesButton, "SendInvites");
        labels.put(this.participantsLabel, "Participants");
        labels.forEach((key, val) -> {
            var translation = this.translationSupplier.getTranslation(val);
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
        System.out.println("SEND INVITES");
    }

    /**
     * Method to delete a participant from the event
     * @param participant participant object
     */
    public void deleteParticipant(Participant participant) {
        event.getParticipants().remove(participant);
        //serverUtils.deleteParticipant(participant);
        setEvent(event);
    }

    /**
     * Method to add a participant to the event
     */
    public void addParticipant() {
        mainCtrl.showCreateParticipant(event);
        setEvent(event);
    }

    /**
     * Method to edit a participant
     * @param participant participant object
     */
    public void editParticipant(Participant participant) {
        mainCtrl.showEditParticipant(participant);
        setEvent(event);
    }

    /**
     * Setter for event name
     * @param eventName event name
     */
    public void setEventNameLabel(String eventName) {
        eventNameLabel.setText(eventName);
    }

    public void addExpense() {
        mainCtrl.showAddExpense();
        setEvent(event);
    }
    public void editExpense(Expense expense) {
        mainCtrl.showEditExpense(expense);
        setEvent(event);
    }
    public void deleteExpense(Expense expense) {
        mainCtrl.getSessionHandler().sendExpense(expense, "delete");
        setEvent(event);
    }
}