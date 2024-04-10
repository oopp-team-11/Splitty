package client.scenes;

import client.interfaces.Translatable;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;

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
    public Label participantIBANLabel;

    @FXML
    public Label participantBICLabel;

    @FXML
    private Label editParticipantLabel;

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
        labels.put(this.firstName, "FirstName");
        labels.put(this.lastName, "LastName");
        labels.put(this.editParticipantLabel, "EditAParticipant");
        labels.put(this.editParticipantButton, "Edit");
        labels.put(this.abortEditButton, "Cancel");
        labels.put(this.participantFirstNameLabel, "ParticipantFirstName");
        labels.put(this.participantLastNameLabel, "ParticipantLastName");
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
        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();

        if(firstNameString.isEmpty()){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("First name field is empty, please fill in a first name.");
            alert.showAndWait();
        } else if (lastNameString.isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Last name field is empty, please fill in a last name.");
            alert.showAndWait();
        } else {

            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Are you sure you want to add this participant?");
            var result = alert.showAndWait();
            if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                participant.setFirstName(firstName.getText());
                participant.setLastName(lastName.getText());
                participant.setIban(iban.getText());
                participant.setBic(bic.getText());


                mainCtrl.getSessionHandler().sendParticipant(participant, "update");

                mainCtrl.showEventOverview();
            }else {
                mainCtrl.showEventOverview();
            }
        }
    }

    /**
     * Method for aborting creating participant
     */
    public void abort() {
        mainCtrl.showEventOverview();
    }
}