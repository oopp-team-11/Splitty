package client.scenes;

import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

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
    private final ServerUtils serverUtils;
    private TranslationSupplier translationSupplier;

    /**
     * Constructor for the EditParticipant.fxml scene controller.
     * Creates a new ServerUtils instance.
     * @param mainCtrl reference to the main scene controller
     */
    @Inject
    public EditParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = new ServerUtils();
    }

    /**
     * Setter for participant
     * @param participant reference to participant object that is edited
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
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


        try {
            serverUtils.editParticipant(participant.getId(), firstNameString, lastNameString,
                    emailString, ibanString, bicString, "http://localhost:8080");
        }
        catch (IOException | InterruptedException e)
        {
            System.err.println("Error while sending edit request to server");
            return;
        }

        mainCtrl.showStartScreen(); // todo: Change that to event screen when there is one

    }

    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
    }
}