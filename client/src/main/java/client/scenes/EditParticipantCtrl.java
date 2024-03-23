package client.scenes;

import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Client controller for the EditParticipant.fxml scene
 */
public class EditParticipantCtrl {
    @FXML
    private Button editBtn;

    @FXML
    private Label editParticipantLabel;

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

    public void setTranslationSupplier(TranslationSupplier tl) {
        this.translationSupplier = tl;
        this.translate();
    }

    private void translate() {
        if (this.translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.email, "Email");
        labels.put(this.firstName, "FirstName");
        labels.put(this.lastName, "LastName");
        labels.put(this.editParticipantLabel, "EditAParticipant");
        labels.put(this.editBtn, "Edit");
        labels.forEach((k, v) -> {
            var translation = this.translationSupplier.getTranslation(v);
            if (translation == null) return;
            if (k instanceof Labeled)
                ((Labeled) k).setText(translation.replaceAll("\"", ""));
            if (k instanceof TextField)
                ((TextField) k).setPromptText(translation.replaceAll("\"", ""));
        });
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
}