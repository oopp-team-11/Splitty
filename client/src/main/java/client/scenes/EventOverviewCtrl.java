package client.scenes;

import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/***
 * class CreateParticipantController
 */
public class EventOverviewCtrl {
    @FXML
    public Label invitationCode;
    @FXML
    private Label eventNameLabel;
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

            Label nameLabel = new Label(participant.getFirstName() + " " + participant.getLastName());
            nameLabel.setFont(new javafx.scene.text.Font("Arial", 16));
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

            namesVbox.getChildren().add(nameLabel);
            deleteVbox.getChildren().add(deleteLabel);
            editVbox.getChildren().add(editLabel);
        }
    }

    /**
     * When button gets clicked, trigger send invites method
     */
    public void sendInvites() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(event.getId().toString());
        clipboard.setContent(content);
        invitationCode.setText("Code moved to \nClipboard.");
    }
    /**
     * When button gets clicked, trigger go to homeScreen method
     */
    public void goToHomeScreen() {
        mainCtrl.showStartScreen();
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
        eventNameLabel.setText("Title: " + eventName);
    }

}