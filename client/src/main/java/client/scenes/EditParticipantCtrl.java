package client.scenes;

import client.utils.EventStompSessionHandler;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Client controller for the EditParticipant.fxml scene
 */
public class EditParticipantCtrl {

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;

    private Participant participant;
    private final MainCtrl mainCtrl;
    private EventStompSessionHandler sessionHandler;

    /**
     * Constructor for the EditParticipant.fxml scene controller.
     * Creates a new ServerUtils instance.
     * @param mainCtrl reference to the main scene controller
     */
    @Inject
    public EditParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for participant
     * @param participant reference to participant object that is edited
     * @param sessionHandler current session handler with websocket connection
     */
    public void setParticipant(Participant participant, EventStompSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
        this.participant = participant;
        firstName.setText(participant.getFirstName());
        lastName.setText(participant.getLastName());
        email.setText(participant.getEmail());
        bic.setText(participant.getBic());
        iban.setText(participant.getIban());
    }

    /**
     * When button gets clicked, send PUT request to participants
     */
    public void onEdit() {

        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();
        String ibanString = iban.getText();
        String bicString = bic.getText();
        String emailString = email.getText();

        this.participant.setFirstName(firstNameString);
        this.participant.setLastName(lastNameString);
        this.participant.setIban(ibanString);
        this.participant.setBic(bicString);
        this.participant.setEmail(emailString);

        try {
            sessionHandler.sendParticipant(participant, "update");
        }
        catch (Exception e)
        {
            System.err.println("Error while sending edit request to server");
            return;
        }

        mainCtrl.showEventOverview(participant.getEvent()); // todo: Change that to event screen when there is one

    }

    /**
     * Button function to abort editing participant
     */
    public void abort() {
        mainCtrl.showEventOverview(participant.getEvent());
    }
}