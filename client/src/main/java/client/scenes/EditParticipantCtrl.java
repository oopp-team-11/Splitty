package client.scenes;

import client.interfaces.Translatable;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Client controller for the EditParticipant.fxml scene
 */
public class EditParticipantCtrl implements Translatable {
    @FXML
    public Button abortEditButton;

    @FXML
    public Button editParticipantButton;

    @FXML
    public Label participantFirstNameLabel;

    @FXML
    public Label participantLastNameLabel;

    @FXML
    public Label participantEmailLabel;

    @FXML
    public Label participantIBANLabel;

    @FXML
    public Label participantBICLabel;

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
        firstName.setText(participant.getFirstName());
        lastName.setText(participant.getLastName());
        email.setText(participant.getEmail());
        iban.setText(participant.getIban());
        bic.setText(participant.getBic());
    }

    /**
     * Translates the current scene using a translationSupplier
     * @param translationSupplier an instance of a translationSupplier. If null, the default english will be displayed.
     */
    @Override
    public void translate(TranslationSupplier translationSupplier) {
        if (translationSupplier == null) return;
        Map<Control, String> labels = new HashMap<>();
        labels.put(this.email, "Email");
        labels.put(this.firstName, "FirstName");
        labels.put(this.lastName, "LastName");
        labels.put(this.editParticipantLabel, "EditAParticipant");
        labels.put(this.editParticipantButton, "Edit");
        labels.put(this.abortEditButton, "Cancel");
        labels.put(this.participantFirstNameLabel, "ParticipantFirstName");
        labels.put(this.participantLastNameLabel, "ParticipantLastName");
        labels.put(this.participantEmailLabel, "ParticipantEmail");
        labels.put(this.participantIBANLabel, "ParticipantIBAN");
        labels.put(this.participantBICLabel, "ParticipantBIC");
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
     * When button gets clicked, send PUT request to participants
     */
    public void onEdit() {

        participant.setFirstName(firstName.getText());
        participant.setLastName(lastName.getText());
        participant.setEmail(email.getText());
        participant.setIban(iban.getText());
        participant.setBic(bic.getText());


        mainCtrl.getSessionHandler().sendParticipant(participant, "update");

        mainCtrl.showEventOverview();

    }

    /**
     * Method for aborting creating participant
     */
    public void abort() {
        mainCtrl.showEventOverview();
    }
}