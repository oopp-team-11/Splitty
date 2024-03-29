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

import java.io.IOException;
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
    public Label participantEmailLabel;

    @FXML
    public Label participantIBANLabel;

    @FXML
    public Label participantBICLabel;

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
        this.fileSystemUtils = new FileSystemUtils();
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
        labels.put(this.email, "Email");
        labels.put(this.firstName, "FirstName");
        labels.put(this.lastName, "LastName");
        labels.put(this.addParticipantLabel, "AddAParticipant");
        labels.put(this.createBtn, "Create");
        labels.put(this.cancelBtn, "Cancel");
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
     * Setter for event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * When button gets clicked, send POST request to participants
     * @throws IOException when error occurred while sending the request to server
     * @throws InterruptedException when error occurred while sending the request to server
     */
    public void onCreate() throws IOException, InterruptedException {
        System.out.println("ONCREATE");

        String firstNameString = firstName.getText();
        String lastNameString = lastName.getText();
        String ibanString = iban.getText();
        String bicString = bic.getText();
        String emailString = email.getText();

        if(firstNameString.isEmpty() || lastNameString.isEmpty())
        {
            System.err.println("Error. First name and last name are mandatory.");
            return;
        }


        mainCtrl.getSessionHandler().sendParticipant(
                new Participant(mainCtrl.getDataHandler().getEvent(),
                        firstNameString,
                        lastNameString,
                        emailString,
                        ibanString,
                        bicString
                ),
                "create"
        );

        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());

    }

    /**
     * Method for aborting creating participant
     */
    public void abort() {
        mainCtrl.showEventOverview(mainCtrl.getDataHandler().getEvent());
    }
}