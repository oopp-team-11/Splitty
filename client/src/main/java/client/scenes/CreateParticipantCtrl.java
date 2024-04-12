package client.scenes;

import client.interfaces.Translatable;
import client.utils.FileSystemUtils;
import client.utils.ServerUtils;
import client.utils.TranslationSupplier;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.Map;

/***
 * class CreateParticipantController
 */
public class CreateParticipantCtrl implements Translatable {

    @FXML
    public Label addParticipantLabel;

    @FXML
    public Button createBtn;

    @FXML
    public Button cancelBtn;

    @FXML
    public Label participantFirstNameLabel;

    @FXML
    public Label participantLastNameLabel;

    @FXML
    public Label participantIBANLabel;

    @FXML
    public Label participantBICLabel;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;

    private Event event;
    private MainCtrl mainCtrl;

    private FileSystemUtils fileSystemUtils;
    private ServerUtils serverUtils;

    /***
     * constructor with injection
     * @param mainCtrl
     */
    @Inject
    public CreateParticipantCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.fileSystemUtils = new FileSystemUtils(mainCtrl.getTranslationSupplier());
        this.serverUtils = new ServerUtils();
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
        labels.put(this.addParticipantLabel, "AddAParticipant");
        labels.put(this.createBtn, "Create");
        labels.put(this.cancelBtn, "Cancel");
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
     * Setter for event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
        firstName.clear();
        lastName.clear();
        iban.clear();
        bic.clear();
    }

    /**
     * When button gets clicked, send POST request to participants
     */
    public void onCreate() {

        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();
        String ibanString = iban.getText();
        String bicString = bic.getText();

        if(firstNameString.isEmpty()){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier()
                    .getTranslation("ParticipantFirstNameEmpty")
                    .replaceAll("\"", ""));
            alert.showAndWait();
        } else if (lastNameString.isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier()
                    .getTranslation("ParticipantLastNameEmpty")
                    .replaceAll("\"", ""));
            alert.showAndWait();
        } else {

            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(mainCtrl.getTranslationSupplier()
                    .getTranslation("ConfirmationCreateParticipant")
                    .replaceAll("\"", ""));
            var result = alert.showAndWait();
            if (result.isPresent() && !result.get().equals(ButtonType.CANCEL)){
                mainCtrl.getSessionHandler().sendParticipant(
                        new Participant(mainCtrl.getDataHandler().getEvent(),
                                firstNameString,
                                lastNameString,
                                ibanString,
                                bicString
                        ),
                        "create"
                );

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