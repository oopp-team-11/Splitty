package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/***
 * class CreateParticipantController
 */
public class EventOverviewCtrl {
    @FXML
    private Label eventNameLabel;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private Label addParticipantLabel;
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

            addParticipantLabel.onMouseClickedProperty()
                .set(event1 -> addParticipant());

            namesVbox.getChildren().add(label);
            deleteVbox.getChildren().add(deleteLabel);
            editVbox.getChildren().add(editLabel);
        }
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

    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
    }
}